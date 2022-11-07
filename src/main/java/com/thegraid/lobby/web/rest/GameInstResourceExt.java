package com.thegraid.lobby.web.rest;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thegraid.lobby.domain.GameInst;
import com.thegraid.lobby.domain.GameInstProps;
import com.thegraid.lobby.domain.GamePlayer;
import com.thegraid.lobby.domain.Player;
import com.thegraid.lobby.domain.User;
import com.thegraid.lobby.repository.GameInstPropsRepository;
import com.thegraid.lobby.repository.GameInstRepository;
import com.thegraid.lobby.repository.GamePlayerRepositoryExt;
import com.thegraid.lobby.security.AuthoritiesConstants;
import com.thegraid.lobby.service.GameInstService;
import com.thegraid.lobby.service.UserService;
import com.thegraid.lobby.service.dto.GameInstDTO;
import com.thegraid.share.LobbyLauncher.GameResults;
import com.thegraid.share.LobbyLauncher.LaunchInfo;
import com.thegraid.share.LobbyLauncher.LaunchResults;
import com.thegraid.share.auth.AuthUtils;
import com.thegraid.share.auth.AuthUtils.RestTemplateWithAuth;
import com.thegraid.share.auth.NotGameException;
import com.thegraid.share.auth.TicketService.GameTicketService;
import com.thegraid.share.domain.intf.IGameInstDTO;
import com.thegraid.share.domain.intf.IGameInstPropsDTO;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@Primary
@RestController
@RequestMapping("lobby/gi")
public class GameInstResourceExt extends GameInstResource {

    // Using JWT: https://medium.com/innoventes/spring-boot-with-jwt-7970e5be4540

    static final Logger log = LoggerFactory.getLogger(GameInstResource.class);

    /**
     * launch game using HTTP to gamma-web...LaunchService.
     */
    @Component("gameLauncher")
    public static class RestLauncher {

        private GameInstResourceExt gameInstResourceExt;

        // Rest(GameInstResourceExt gameInstResourceExt) {
        // this.gameInstResourceExt = gameInstResourceExt; // circular dependency
        // }

        // To be a Proxy to GameLauncher running on gamma[567].thegraid.com
        // using HTTP-REST:
        // Send PUT to start a new game
        // POST to 'start/terminate'? no real need from here. (client has ClockCtrl)
        // Send GET to get status
        // Send DEL to terminate?
        // Send message/request to game Server(giid) get actual status.

        @Value("${gamma.gameLaunchPath}")
        private String gameLaunchPath;

        @Value("${gamma.launchAuthPath}")
        private String launchAuthPath;

        private Map<String, String> launchTokens = new HashMap<String, String>();

        private Map<String, RestTemplate> hostTemplates = new HashMap<String, RestTemplate>();

        /**
         * obtain/create a RestTemplate that inserts the CURRENT JWT/launchToken for the given hostPort.
         * @param hostPort
         * @return
         */
        public RestTemplate getRestTemplate(String hostPort) {
            RestTemplate restTemplate = hostTemplates.get(hostPort);
            if (restTemplate == null) {
                restTemplate = new RestTemplateWithAuth(() -> launchTokens.get(hostPort));
            }
            return restTemplate;
        }

        private String loginToLauncher(String hostPort) {
            RestTemplate rest = getRestTemplate(hostPort);
            String launchToken = AuthUtils.loginToUrl(rest, "https://" + hostPort + launchAuthPath, "admin", "admin");
            launchTokens.put(hostPort, launchToken);
            return launchToken;
        }

        /**
         * Stub that invokes launcher implementation.
         * Using HTTP/REST invocation (for load-balancing)
         *
         * @param hostPort "host.domain:port"
         * @return results {HostURL, StartTime}
         */
        public LaunchResults launchPost(String hostPort, LaunchInfo launchInfo) {
            return launchPostAttempt(hostPort, launchInfo, 0);
        }

        private LaunchResults launchPostAttempt(String hostPort, LaunchInfo launchInfo, int attempt) {
            // https://spring.io/guides/gs/consuming-rest/
            // https://www.baeldung.com/rest-template
            if (launchTokens.get(hostPort) == null) loginToLauncher(hostPort);
            if (launchTokens.get(hostPort) != null) {
                try {
                    RestTemplate rest = getRestTemplate(hostPort);
                    String launchUrl = "https://" + hostPort + gameLaunchPath;
                    return rest.postForObject(launchUrl, launchInfo, LaunchResults.Impl.class);
                } catch (RestClientException ex) {
                    String msg = ex.getMessage();
                    if (msg.startsWith("401 Unauthorized") && attempt < 1) {
                        return launchPostAttempt(hostPort, launchInfo, attempt++);
                    }
                    log.error("launchPost: FAILED - " + msg);
                }
            }
            return new LaunchResults.Impl(); // login OR launch failed
        }

        public GameResults abortIfRunning(Long giid) {
            log.warn("HttpGameLauncher: Not aborting " + giid); // TODO: a new REST endpoint
            // finished(new GameResults.Impl(giid)); // with null values...
            return null;
        }

        public void finished(GameResults results) {
            gameInstResourceExt.recordResults(results, null);
        }
    }

    /** URL path for redirect: to gpEdit; ALSO: path to view gi/edit${GameClassName} */
    @Value("${gamma.gameEditPath}")
    private String gameEditPath; // 'game-inst/%s/edit#role=%s'

    // Two cases: return a view "gi/foo" with Model *OR* redirect to a URL: "/gi/edit/{role}/{giid}"

    private String editPath(String role, Long giid) {
        return String.format(gameEditPath, giid, role);
    }

    private String editUrl(String role, GameInst gameInst) {
        return editPath(role, gameInst.getId());
    }

    // TODO: try use Spring's ObjectMapper.
    // https://stackoverflow.com/questions/30060006/how-do-i-obtain-the-jackson-objectmapper-in-use-by-spring-4-1
    // For now, add JavaTimeModule()
    // https://github.com/FasterXML/jackson-modules-java8/tree/2.14/datetime
    static ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build().setDefaultPropertyInclusion(Include.NON_NULL);

    private String jsonify(Object obj) {
        // https://www.baeldung.com/spring-boot-customize-jackson-objectmapper#1-objectmapper
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON conversion failed", e);
        }
    }

    private <T> T toType(Object obj, Class<T> clazz) {
        try {
            String content = jsonify(obj);
            return mapper.readValue(content, clazz);
        } catch (JsonProcessingException ex) {
            log.error("toType: FAILED - " + ex.getMessage());
            return null;
        }
    }

    /** GamePlayer.role value for PlayerA */
    public static final String ROLE_A = "A";

    /** GamePlayer.role value for PlayerB */
    public static final String ROLE_B = "B";

    public static String otherRole(String role) {
        return role.equals(ROLE_A) ? ROLE_B : ROLE_A;
    }

    @Value("${gamma.allowResetGiid}")
    private Boolean allowResetGiid;

    @Value("${gamma.launchHosts")
    private String[] launchHosts;

    @Value("${gamma.gameLoginPath}")
    private String gameLoginPath;

    @Autowired
    public RestLauncher gameLauncher;

    @Autowired
    private UserService userService;

    @Autowired
    private GameTicketService gameTicketService;

    private final GameInstService gameInstService;

    private final GameInstRepository gameInstRepository;

    private final GameInstPropsRepository gameInstPropsRepository;

    private final GamePlayerRepositoryExt gamePlayerRepository;

    // Constructor
    public GameInstResourceExt(
        GameInstService gameInstService,
        GameInstRepository gameInstRepository,
        GameInstPropsRepository gameInstPropsRepository,
        GamePlayerRepositoryExt gamePlayerRepository
    ) {
        super(gameInstService, gameInstRepository);
        this.gameInstService = gameInstService;
        this.gameInstRepository = gameInstRepository;
        this.gameInstPropsRepository = gameInstPropsRepository;
        this.gamePlayerRepository = gamePlayerRepository;
    }

    public Map<String, GamePlayer> findGamePlayerByRole(GameInst gameInst) {
        // GameInst & Player each have a Set of GamePlayers
        List<GamePlayer> gamePlayers = gamePlayerRepository.getGamePlayers(gameInst);
        if (gamePlayers.isEmpty()) return null;
        Map<String, GamePlayer> rv = new HashMap<String, GamePlayer>();
        for (GamePlayer gp : gamePlayers) {
            rv.put(gp.getRole(), gp);
        }
        return rv;
    }

    public boolean hasUserAuth(User member) {
        // re-fetch User with Authorities; ASSERT .isPresent()
        Optional<User> optUserWithAuths = userService.getUserWithAuthoritiesByLogin(member.getLogin());
        if (!optUserWithAuths.isPresent()) return false;
        // confirm that given member has ROLE_USER
        return optUserWithAuths
            .get()
            .getAuthorities()
            .stream()
            .anyMatch(auth -> {
                return auth.getName().equals(AuthoritiesConstants.USER);
            });
    }

    /** used for 'testing' with resetGiid
     * @return */
    private Authentication loginAs(User member, HttpServletRequest request) {
        if (!hasUserAuth(member)) return null; // cannot happen because User is known to own the Player[role]
        Authentication authToken0 = SecurityContextHolder.getContext().getAuthentication();
        String loginid = member.getLogin();
        // are we *already* logged-in as member?
        if (loginid.equals(authToken0.getName())) {
            final String jsessions = AuthUtils.getCookieValue("JSESSIONID", request);
            log.debug("loginAs: already \'{}\' JSESSIONID= {}", loginid, jsessions);
            return authToken0;
        }
        // create AuthenticationToken with ROLE_USER
        String passwd = member.getPassword();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        Authentication authToken = new UsernamePasswordAuthenticationToken(loginid, passwd, authorities);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
        request.getSession(true).setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        String jsessions = AuthUtils.getCookieValue("JSESSIONID", request);
        log.debug("loginAs: using loginid={} for member={} JSESSIONID=", loginid, member, jsessions);
        return authToken;
    }

    private void resetGame(GameInst gameInst) {
        Long giid = gameInst.getId();
        gameLauncher.abortIfRunning(giid);
        GameResults reset = new GameResults.Impl(giid); // true, null...
        gameInst.setStarted(null);
        recordResults(reset, gameInst);
        // Now: launchGameInstIfReady()
    }

    /** suitable for callback when Launcher returns GameResults */
    private void recordResults(GameResults results, GameInst gameInst) {
        if (gameInst == null) {
            gameInst = gameInstRepository.findById(results.getId()).get();
        }
        gameInst.setFinished(results.getFinished());
        gameInst.setScoreA(results.getScoreA());
        gameInst.setScoreB(results.getScoreB());
        gameInst.setTicks(results.getTicks());
        gameInstRepository.save(gameInst); // reset fields in database.
    }

    // https://lobby2.thegraid.com:8442/lobby/gi/redit/A/154
    @RequestMapping(value = "redit/{giid}/{role:[AB]}")
    //@ResponseBody
    public RedirectView resetGiid(@PathVariable("role") String role, @PathVariable("giid") Long giid, HttpServletRequest request) {
        Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid);
        if (gameInstOpt.isEmpty()) throw new NotGameException("Invalid gameInst id: " + giid);
        // resetGiid ONLY works when using when enabled in application.yml
        if (!allowResetGiid) {
            throw new IllegalStateException("Not configured for resetGiid: " + gameLauncher);
        } else {
            (gameLauncher).gameInstResourceExt = this; // @AutoWired is circular
        }
        GameInst gameInst = gameInstOpt.get();
        Map<String, GamePlayer> gamePlayers = findGamePlayerByRole(gameInst);
        // find User in given role:
        GamePlayer gamePlayer1 = gamePlayers.get(role);
        Player player = gamePlayer1.getPlayer();
        User user = player.getUser();
        log.debug("\nPlayer: {}, \nUser: {}", player, user);
        if (loginAs(user, request) == null) {
            throw new IllegalStateException("Could not login as " + user.getLogin());
        }
        log.debug("Principal requesting reset: {} of gameInst= {}", user.getLogin(), gameInst);

        resetGame(gameInst); // nullify started and results [does not change hostUrl]

        // get [login] Url with validationToken(user,gpid)
        String redirectUrl = launchGame(gamePlayers, role, request); // gamePlayer[s]->gameInst->giid
        log.debug("new RedirectView({})", redirectUrl);
        return new RedirectView(redirectUrl);
    }

    @RequestMapping(value = "afterLogin/{valid}")
    public RedirectView afterLogin(
        @PathVariable(value = "valid", required = false) Boolean valid,
        @RequestParam("P") String p, // hash
        @RequestParam("T") String t, // timelimit
        @RequestParam("U") String u, // user.loginId
        @RequestParam("V") String v, // gpid [giid, gamePlayer -> {role,display_client}]
        HttpServletRequest request
    ) {
        final String home = ""; // HOME "https://lobby2.thegraid.com:8442" https://stackoverflow.com/questions/52689585/how-to-redirect-at-home-page-if-any-router-does-not-exist-in-jhipster-angular-4
        final String jsessions = AuthUtils.getCookieValue("JSESSIONID", request);
        final String qsf = "gi/afterLogin/%s?P=%s&T=%s&U=%s&V=%s";
        final String rdv = String.format(qsf, valid, p, t, u, v);
        log.debug("afterLogin: {} {}", rdv, jsessions);
        final boolean isValid = gameTicketService.validateTicket(p, t, u, v, jsessions);
        if (!isValid) {
            return new RedirectView(home);
        }
        String loginid = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loginid.equals(u)) return new RedirectView(home);
        //User user = userService.getUserWithAuthoritiesByLogin(loginid).get();
        Long gpid = Long.parseLong(v);
        GamePlayer gamePlayer = gamePlayerRepository.getReferenceById(gpid);
        Player player = gamePlayer.getPlayer();
        //String role = gamePlayer.getRole();
        String dispC = player.getDisplayClient();
        return new RedirectView(dispC);
    }

    // Start game [delegate to GameLauncher] & download [JNLP? Flash? whatever...]
    /**
     * Start a game.
     * Send info to LauncherService; redirect to login URL if launch works.
     * @param gamePlayers for each of ROLE_A & ROLE_B
     * @param role the initiating GamePlayer; is active/online/driving this request
     * @param request - holds session_id for current player
     * @return the redirect url (success:login or fail:edit)
     */
    private String launchGame(Map<String, GamePlayer> gamePlayers, String role, HttpServletRequest request) {
        GameInst gameInst = gamePlayers.get(ROLE_A).getGameInst(); // each GamePlayer has same GameInst
        Long giid = gameInst.getId();

        String baseUrl = request.getRequestURL().toString(); //baseURL(request);
        log.warn("launchGame: {} from {}", gameInst, baseUrl); // identify this lobby?
        String url1 = loginUrl(gamePlayers.get(role), request);
        // why synchronized here? maybe multiple client requests. ?SAME? Entity (no, entity is per-hibernate-session)
        // we are trusting that hibernate will 'sync' entity state across servers?
        synchronized (gameInst) {
            if (gameInst.getStarted() != null) {
                log.warn("launchGame: Game already started: {} @ {}", url1, gameInst.getStarted());
                return url1; // [re]start login & loading displayClient
            }
            Optional<GameInstProps> gamePropsOpt = gameInstPropsRepository.findById(giid);
            GameInstProps gameProps = gamePropsOpt.isPresent() ? gamePropsOpt.get() : new GameInstProps();

            LaunchInfo info = new LaunchInfo();
            info.gameInst = toType(gameInst, IGameInstDTO.Impl.class);
            info.gameProps = toType(gameProps, IGameInstPropsDTO.Impl.class);
            info.gpidA = gamePlayers.get(ROLE_A).getId();
            info.gpidB = gamePlayers.get(ROLE_B).getId();
            info.resultTicket = this.getValidationToken(gamePlayers.get(role), request);
            log.warn("launchGame: launchInfo={}", jsonify(info));

            String hostUrl = gameInst.getHostUrl(); // "game5.gamma.com:8445"
            if (hostUrl == null || hostUrl.isEmpty()) {
                int choose = (int) Math.random() * launchHosts.length;
                hostUrl = launchHosts[choose];
                log.debug("launchGame: choose hostUrl = {}", hostUrl);
                gameInst.setHostUrl(hostUrl); // set so Launcher can find in gameInst
                gameInstRepository.save(gameInst);
            }
            log.info("launchGame: launch giid {} from hostUrl: {}", giid, hostUrl);

            // HttpInvoker-based Launcher; wait and parse the results into LaunchResults
            LaunchResults results = gameLauncher.launchPost(hostUrl, info); // results now simply: "started"
            // Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid); // try get NEW values
            // gameInst = gameInstOpt.get(); // ASSERT: gameInstOpt.isPresent()
            log.debug("Launched giid: {}, results={}", giid, jsonify(results));

            if (results == null || results.getStarted() == null) {
                log.warn("Game launch failed: {} \nfrom {} \nresults={}", gameInst, baseUrl, jsonify(results));
                // TODO: something to provoke notification to the Member(s)' web page.
                return editUrl(role, gameInst) + "#fail";
            }

            gameInst.setStarted(results.getStarted());
            gameInstRepository.save(gameInst); // update started (& hostUrl)
            url1 = loginUrl(gamePlayers.get(role), request);
            // other player needs to login to Lobby to obtain their login to Game

        }
        // Game is launched, let players login (&start loading DisplayClient)
        log.debug("url1={}", url1);
        // use GammaJMSBroadcaster.JSONP to send other member to login; (and then --> goActive?)
        // User member2 = gamePlayer2.getPlayer().getUser();
        // sendJsonp(member2, "events.nextpage", String.format("\"%s\"", url2));
        log.debug("launchGame: Launched={} clients: rv={}", giid, url1);
        return url1;
    }

    URL newURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            log.warn("url {}: {}", url, e);
            return null;
        }
    }

    LaunchInfo getLaunchInfo(GameInst gameInst, Map<String, GamePlayer> gamePlayers, String role, HttpServletRequest request) {
        Long giid = gameInst.getId();
        Optional<GameInstProps> gamePropsOpt = gameInstPropsRepository.findById(giid);
        GameInstProps gameProps = gamePropsOpt.isPresent() ? gamePropsOpt.get() : new GameInstProps();

        log.warn("launchGame: gameLauncher={}", this.gameLauncher);
        log.warn("launchGame: gameProps={}", gameProps);

        LaunchInfo info = new LaunchInfo();
        info.gameInst = toType(gameInst, IGameInstDTO.Impl.class);
        info.gameProps = toType(gameProps, IGameInstPropsDTO.Impl.class);
        info.gpidA = gamePlayers.get(ROLE_A).getId();
        info.gpidB = gamePlayers.get(ROLE_B).getId();
        info.resultTicket = this.getValidationToken(gamePlayers.get(role), request);
        log.warn("launchGame: launchInfo={}", jsonify(info));
        return info;
    }

    /**
     * login to game server.
     * @param gamePlayer -> gameInst -> hostUrl
     * @param request -> cookies -> JSESSIONID
     * @return gameLoginPath(hostPort, giid, token)
     */
    private String loginUrl(GamePlayer gamePlayer, HttpServletRequest request) {
        GameInst gameInst = gamePlayer.getGameInst();
        String hostPort = gameInst.getHostUrl();
        Long giid = gameInst.getId();
        String token = getValidationToken(gamePlayer, request);
        String url = String.format(gameLoginPath, hostPort, giid, token);
        return url;
    }

    // see also: https://www.baeldung.com/spring-security-oauth-jwt
    /**
     * build a validation token for the GamePlayer's current request/session.
     * @param gamePlayer
     * @param request
     * @return token (or null if no JSESSSIONID)
     */
    private String getValidationToken(GamePlayer gamePlayer, HttpServletRequest request) {
        String jsessions = AuthUtils.getCookieValue("JSESSIONID", request);
        String loginid = gamePlayer.getPlayer().getUser().getLogin();
        log.debug("getValidationToken: for {} JSESSIONID={}", loginid, jsessions);
        if (jsessions == null) {
            log.warn("Session expired");
            jsessions = ""; // TODO: redirect to login page
            //return null;
        }
        // Jwt jwt = new Jwt();
        // JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        // Optional<String> username = SecurityUtils.getCurrentUserLogin();
        Optional<User> optUser = userService.getUserWithAuthoritiesByLogin(loginid);
        if (optUser.isEmpty()) return null;
        User user = optUser.get();
        String username = user.getLogin();
        Long validTime = TimeUnit.HOURS.toMillis(3);
        Long gpid = gamePlayer.getId(); // @NotNull (was gamePlayer.getRole())
        String token = gameTicketService.getTicket(username, validTime, gpid, jsessions); // includes U=loginId
        return token;
    }

    // InfoService for GamaLauncher -- obsolete
    @RequestMapping("info/{giid}")
    public GameInstDTO getGameInfo(@PathVariable("giid") Long giid, HttpServletRequest request) {
        log.debug("getGameInfo({})", giid);
        Optional<GameInstDTO> optGameInstDTO = gameInstService.findOne(giid);
        GameInstDTO dto = optGameInstDTO.get();
        //Long propsId = dto.getPropsId();
        //gameInstPropsService
        // TODO: include gameInstProps
        // gameInstDTO implements com.thegraid.lobby.domain.intf.IGameInstDTO
        log.debug("getGameInfo({}) GameInstDTO = {{}}", giid, dto);
        return dto;
    }

    // doc says RequestParam works for query-params AND form-data
    // callback from Launcher [TDB] - obsolete: launch returns LaunchResults/JSON -> save(started)
    // may need something for end of game?
    @RequestMapping("update/{giid}")
    public boolean updateGameInfo(
        @PathVariable("giid") Long giid,
        @RequestParam(name = "time") Instant timestamp,
        @RequestParam(name = "hostUrl") String hostUrl
    ) {
        if (timestamp == null) {
            log.error("Game Launch failed: giid={} @ {}", giid, timestamp);
            return true;
        }
        Instant started = timestamp;
        Optional<GameInstDTO> optGameInstDTO = gameInstService.findOne(giid);
        GameInstDTO dto = optGameInstDTO.get();
        try {
            dto.setStarted(started); // mark start time
            dto.setHostUrl(hostUrl); // on which server
            gameInstService.save(dto); // back to database.
        } catch (Exception ex) {
            log.error("while gameInstService.save(started {}): {}", giid, ex);
            return false;
            // not clear what can we do about it; retry?
            // the game is presumably already in progress
        }
        return true;
    }
}

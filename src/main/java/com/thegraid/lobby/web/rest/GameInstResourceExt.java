package com.thegraid.lobby.web.rest;

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
@RequestMapping("gammaDS/gi")
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

    /** used for 'testing' with resetGiid
     * @return */
    private Authentication loginAs(User member) {
        // re-fetch User with Authorities; ASSERT .isPresent()
        Optional<User> optUserWithAuths = userService.getUserWithAuthoritiesByLogin(member.getLogin());
        // confirm that given member has ROLE_USER
        if (
            !optUserWithAuths
                .get()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> {
                    return auth.getName().equals(AuthoritiesConstants.USER);
                })
        ) return null; // cannot happen because User is known to own the Player[role]
        // create AuthenticationToken with ROLE_USER
        String loginid = member.getLogin();
        String passwd = member.getPassword();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
        Authentication token = new UsernamePasswordAuthenticationToken(loginid, passwd, authorities);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);
        SecurityContextHolder.setContext(securityContext);
        log.debug("loginAs: using loginid={} for member={}", loginid, member);
        return SecurityContextHolder.getContext().getAuthentication();
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

    // https://lobby2.thegraid.com:8442/gammaDS/gi/redit/A/154
    @RequestMapping(value = "redit/{role:[AB]}/{giid}") // TODO: redit/{giid}/{rold:[AB]}
    //@ResponseBody
    public RedirectView resetGiid(@PathVariable("role") String role, @PathVariable("giid") Long giid, HttpServletRequest request) {
        Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid);
        if (gameInstOpt.isEmpty()) throw new NotGameException("Invalid gameInst id: " + giid);
        // resetGiid ONLY works when using when enabled in application.yml
        if (!allowResetGiid) {
            throw new IllegalStateException("Not configured for resetGiid: " + gameLauncher);
        } else {
            (gameLauncher).gameInstResourceExt = this;
        }
        GameInst gameInst = gameInstOpt.get();
        Map<String, GamePlayer> gamePlayers = findGamePlayerByRole(gameInst);
        // find User in given role:
        GamePlayer gamePlayer1 = gamePlayers.get(role);
        Player player = gamePlayer1.getPlayer();
        User user = player.getUser();
        log.debug("\nPlayer: {}, \nUser: {}", player, user);
        Principal principal = (Principal) loginAs(user);
        if (principal == null) {
            throw new IllegalStateException("Could not login as " + user.getLogin());
        }
        log.debug("Principal requesting reset: {} of gameInst= {}", principal.getName(), gameInst);

        resetGame(gameInst); // nullify started and results [does not change hostUrl]

        String hostUrl = gameInst.getHostUrl(); // "https://game5.gamma.com:8445/launcher/GameControl/giid"
        log.debug("resetGiid: hostUrl = {}", hostUrl);
        if (hostUrl == null || hostUrl.isEmpty()) {
            hostUrl = launchHosts[0];
            gameInst.setHostUrl(hostUrl); // set so Launcher can find in gameInst
            gameInstRepository.save(gameInst); // the latest target Launcher
        }

        log.info("launch giid {} from hostUrl: {}", giid, hostUrl);
        String redirectUrl = launchGame(gamePlayers, role, request); // gamePlayer[s]->gameInst->giid
        log.debug("new RedirectView({})", redirectUrl);
        return new RedirectView(redirectUrl);
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

        String base = request.getRequestURL().toString(); //baseURL(request);
        log.warn("launchGame: {} from {}", gameInst, base); // identify this lobby?
        String url1 = loginUrl(gamePlayers.get(role), request);
        // why synchronized here? maybe multiple client requests. ?SAME? Entity (no, entity is per-hibernate-session)
        // we are trusting that hibernate will 'sync' entity state across servers?
        synchronized (gameInst) {
            if (gameInst.getStarted() != null) {
                log.warn("Game already started: {} @ {}", url1, gameInst.getStarted());
                return url1; // [re]start login & loading displayClient
            }
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

            // HttpInvoker-based Launcher; wait and parse the results into LaunchResults
            LaunchResults results = gameLauncher.launchPost(gameInst.getHostUrl(), info);
            // Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid); // try get NEW values
            // gameInst = gameInstOpt.get(); // ASSERT: gameInstOpt.isPresent()
            log.debug("Launched giid: {}, results={}", giid, jsonify(results));

            if (results == null || results.getStarted() == null) {
                log.warn("Game launch failed: {} \nfrom {} \nresults={}", gameInst, base, jsonify(results));
                // TODO: something to provoke notification to the Member(s)' web page.
                return editUrl(role, gameInst) + "#fail";
            }
            URL hostUrl = newURL(results.getHostURL());
            String hostPort = hostUrl.getHost() + ":" + hostUrl.getPort();
            gameInst.setHostUrl(hostPort);
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
     * @return
     */
    private String getValidationToken(GamePlayer gamePlayer, HttpServletRequest request) {
        String jsessions = AuthUtils.getCookieValue("JSESSIONID", request);
        String loginid = gamePlayer.getPlayer().getUser().getLogin();
        log.debug("getValidationToken: for {} JSESSIONID={}", loginid, jsessions);
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

    // InfoService for GamaLauncher
    @RequestMapping("info/{giid}")
    public GameInstDTO getGameInfo(@PathVariable("giid") Long giid, HttpServletRequest request) {
        log.debug("getGameInfo({})", giid);
        Optional<GameInstDTO> optGameInstDTO = gameInstService.findOne(giid);
        GameInstDTO dto = optGameInstDTO.get();
        //Long propsId = dto.getPropsId();
        //gameInstPropsService
        // TODO: must include gameInstProps; getPropertyMap();
        // gameInstDTO implements com.thegraid.lobby.domain.intf.IGameInstDTO
        log.debug("getGameInfo({}) GameInstDTO = {{}}", giid, dto);
        return dto;
    }

    // doc says RequestParam works for query-params AND form-data
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

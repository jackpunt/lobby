package com.thegraid.lobby.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thegraid.lobby.auth.NotGameException;
import com.thegraid.lobby.auth.TicketService.GameTicketService;
import com.thegraid.lobby.config.ApplicationProperties;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
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

@Primary
@RestController
@RequestMapping("gammaDS/gi")
public class GameInstResourceExt extends GameInstResource {

    // Using JWT: https://medium.com/innoventes/spring-boot-with-jwt-7970e5be4540

    static final Logger log = LoggerFactory.getLogger(GameInstResource.class);

    /** originally an RMI interface, now a REST inteface */
    public static interface GameLauncher {
        /**
         * @param giid
         * @param gameInst if available, else obtained by findById(giid)
         */
        LaunchResults launchPost(LaunchInfo launchInfo);

        GameResults abortIfRunning(Long giid);

        void finished(GameResults results);

        // using this local class until we build something to replace the InProcLauncher
        // plays the role of AbstractGameLauncher...
        @Component("xgameLauncher")
        public static class Null implements GameLauncher {

            // TODO: track games that are launched but not finished, so we can abort them.

            @Override
            public LaunchResults launchPost(LaunchInfo launchInfo) {
                log.warn("NullGameLauncher: Not launching " + launchInfo.gameInst.getId());
                return null;
            }

            @Override
            public GameResults abortIfRunning(Long giid) {
                log.warn("NullGameLauncher: Not aborting " + giid);
                return null;
            }

            @Override
            public void finished(GameResults results) {
                // TODO parse results and push to database.
                log.warn("NullGameLauncher: Not recording results " + (results != null ? results.getId() : ""));
            }
        }

        /**
         * launch game using HTTP to gamma-web...LaunchService.
         */
        @Component("gameLauncher")
        public static class Rest extends Null implements GameLauncher {

            static class AuthRequest {

                public String username;
                public String password;

                AuthRequest(String username, String password) {
                    this.username = username;
                    this.password = password;
                }
            }

            /**
             * Object returned as body in JWT Authentication.
             */
            static class JWTToken {

                private String idToken;

                JWTToken(String idToken) {
                    this.idToken = idToken;
                }

                @JsonProperty("id_token")
                String getIdToken() {
                    return idToken;
                }

                void setIdToken(String idToken) {
                    this.idToken = idToken;
                }
            }

            private GameInstResourceExt gameInstResourceExt;

            // Rest(GameInstResourceExt gameInstResourceExt) {
            //     this.gameInstResourceExt = gameInstResourceExt; // circular dependency
            // }

            // To be a Proxy to GameLauncher running on gamma[567].thegraid.com
            // using HTTP-REST:
            // Send PUT to start a new game
            // POST to 'start/terminate'? no real need from here. (client has ClockCtrl)
            // Send GET to get status
            // Send DEL to terminate?
            // Send message/request to game Server(giid) get actual status.

            @Value("${gamma.gameLaunchUrl}")
            private String gameLaunchUrl;

            @Value("${gamma.launchAuthUrl}")
            private String launchAuthUrl;

            private String launchToken =
                "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY2NjU1NjUxM30.BbkZF_UkcaulxsFOwZP71dFsFwmduZhVHBZYwCy9H3tRktLDKH487krtxUGK4pfNfxL2nM_MES0q4dDkonRvsg"; // = "..."

            private RestTemplate restTemplate;

            public RestTemplate getRestTemplate() {
                if (restTemplate == null) {
                    restTemplate = new RestTemplate();
                    restTemplate
                        .getInterceptors()
                        .add((request, body, clientHttpRequestExecution) -> {
                            HttpHeaders headers = request.getHeaders();
                            if (!headers.containsKey("Authorization") && launchToken != null) {
                                String token = launchToken.toLowerCase().startsWith("bearer") ? launchToken : "Bearer " + launchToken;
                                request.getHeaders().add("Authorization", token);
                            }
                            return clientHttpRequestExecution.execute(request, body);
                        });
                }
                return restTemplate;
            }

            void loginToLauncher() {
                RestTemplate rest = getRestTemplate();
                AuthRequest ar = new AuthRequest("admin", "admin");
                try {
                    JWTToken jwt = rest.postForObject(launchAuthUrl, ar, JWTToken.class);
                    launchToken = jwt.getIdToken();
                } catch (RestClientException ex) {
                    log.error("loginToLauncher: FAILED - " + ex.getMessage());
                    launchToken = null;
                }
            }

            /**
             * Stub that invokes launcher implementation.
             * Using HTTP/REST invocation (for load-balancing)
             *
             * @return results {HostURL, StartTime}
             */
            @Override
            public LaunchResults launchPost(LaunchInfo launchInfo) {
                return launchPostAttempt(launchInfo, 0);
            }

            private LaunchResults launchPostAttempt(LaunchInfo launchInfo, int attempt) {
                // https://spring.io/guides/gs/consuming-rest/
                // https://www.baeldung.com/rest-template
                RestTemplate rest = getRestTemplate();
                if (launchToken == null) loginToLauncher();
                if (launchToken != null) try {
                    return rest.postForObject(gameLaunchUrl, launchInfo, LaunchResults.Impl.class);
                } catch (RestClientException ex) {
                    String msg = ex.getMessage();
                    if (msg.startsWith("401 Unauthorized") && attempt < 1) {
                        loginToLauncher();
                        return launchPostAttempt(launchInfo, attempt++);
                    }
                    log.error("launchPost: FAILED - " + msg);
                }
                return new LaunchResults.Impl();
            }

            @Override
            public GameResults abortIfRunning(Long giid) {
                log.warn("HttpGameLauncher: Not aborting " + giid); // TODO: a new REST endpoint
                //finished(new GameResults.Impl(giid)); // with null values...
                return null;
            }

            @Override
            public void finished(GameResults results) {
                gameInstResourceExt.recordResults(results, null);
            }
        }
    }

    public static class RedirectDTO {

        public String url;

        RedirectDTO(String url) {
            this.url = url;
        }

        /**
         * suitable for SpringMVC redirect
         *
         * @return HTML body with a redirect link
         */
        public String toBody() {
            return String.format("<body>redirect:<a href=\"%1$s\">%1$s</a></body>", this.url);
        }

        public String toString() {
            return "RedirectDTO{url='" + this.url + "'}";
        }
    }

    /** URL path for redirect: to gpEdit; ALSO: path to view gi/edit${GameClassName} */
    private static String EDIT_PATH = "redit";

    // Two cases: return a view "gi/foo" with Model *OR* redirect to a URL: "/gi/edit/{role}/{giid}"

    private String editPath(String role, Long giid) {
        return String.format("/%s/%s/%d", EDIT_PATH, role, giid);
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

    @Autowired
    public GameLauncher gameLauncher;

    @Autowired
    private ApplicationProperties props;

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

    /** suitable for callback when Launche returns GameResults */
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

    @RequestMapping(value = "redit/{role:[AB]}/{giid}") // TODO: redit/{giid}/{rold:[AB]}
    //@ResponseBody
    public RedirectDTO resetGiid(@PathVariable("role") String role, @PathVariable("giid") Long giid, HttpServletRequest request) {
        Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid);
        if (gameInstOpt.isEmpty()) throw new NotGameException("Invalid gameInst id: " + giid);
        // resetGiid ONLY works when using the "InProc" GameLauncher!
        if (!(gameLauncher instanceof GameLauncher.Rest)) {
            throw new IllegalStateException("Not configured for resetGiid: " + gameLauncher);
        } else {
            ((GameLauncher.Rest) gameLauncher).gameInstResourceExt = this;
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

        resetGame(gameInst);

        String launchUrl = props.gamma.gameLaunchUrl; // where the launcher is listening (https:.../launcher)
        String hostUrl = gameInst.getHostUrl(); // "https://game5.gamma.com:8445/launcher/GameControl/giid"
        String newUrl = replaceHostInUrl(hostUrl, launchUrl); // talk to the REST endpoint on given host
        log.info("temporary setLaunchUrl: {}", newUrl);
        props.gamma.gameLaunchUrl = newUrl;
        String url = launchGame(gamePlayers, role, request);
        props.gamma.gameLaunchUrl = launchUrl; // reset to original

        RedirectDTO redirect = new RedirectDTO(url);
        return redirect;
        // redirect to url (as returned form launchGame)
        //String format = "redirect:%1$s";
        // format = "<body>redirect:<a href=\"%1$s\">%1$s</a></body>"; // set
        // @ResponseBody above
        // url ="https://www.google.com";
        // url = editUrl(role, gameInst);
        //String rv = String.format(format, url);
        //log.warn("rv = {}", rv);
        //return rv;
    }

    /** use HOST:PORT from url with proto/path from gameLaunchUrl */
    private String replaceHostInUrl(String url, String gameLaunchUrl) {
        if (url != null) try {
            URL hostURL = new URL(url);
            URL gameLaunchURL = new URL(gameLaunchUrl); //.getProtocol().split("/")[0];
            URL newLaunchURL = new URL(gameLaunchURL.getProtocol(), hostURL.getHost(), hostURL.getPort(), gameLaunchURL.getPath());
            return newLaunchURL.toString();
        } catch (MalformedURLException e) {
            // assert: we believe this cannot happen: hostUrl & launchUrl are parseable.
            log.warn("unparsed launchUrl: {}", e);
        }
        return url;
    }

    // Start game [delegate to GameLauncher] & download [JNLP? Flash? whatever...]
    /**
     * Start a game.
     * Send info to LauncherService; redirect to login URL if launch works.
     * @param gamePlayer who initiated the Launch (so is active/online/driving this request)
     * @param otherPlayer (maybe not online)
     * @param gameProps
     * @param request
     * @return the redirect url (success:login or fail:edit)
     */
    private String launchGame(Map<String, GamePlayer> gamePlayers, String role, HttpServletRequest request) {
        GameInst gameInst = gamePlayers.get(ROLE_A).getGameInst(); // each GamePlayer has same GameInst
        Long giid = gameInst.getId();

        String base = request.getRequestURL().toString(); //baseURL(request);
        log.warn("launchGame: {} from {}", gameInst, base);
        String url1 = loginUrl(gamePlayers.get(role), request);
        String url2 = loginUrl(gamePlayers.get(otherRole(role)), request);
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
            LaunchResults results = gameLauncher.launchPost(info);
            // Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid); // try get NEW values
            // gameInst = gameInstOpt.get(); // ASSERT: gameInstOpt.isPresent()
            log.debug("Launched giid: {}, results={}", giid, jsonify(results));

            if (results == null || results.getStarted() == null) {
                log.warn("Game launch failed: {} \nfrom {} \nresults={}", gameInst, base, jsonify(results));
                // TODO: something to provoke notification to the Member(s)' web page.
                return editUrl(role, gameInst) + "#fail";
            }
            // TODO: update gameInst with results.started()
        }
        // Game is launched, let players login (&start loading DisplayClient)
        log.debug("url1={}", url1);
        log.debug("url2={}", url2);
        // use GammaJMSBroadcaster.JSONP to send other member to login; (and then --> goActive?)
        // User member2 = gamePlayer2.getPlayer().getUser();
        // sendJsonp(member2, "events.nextpage", String.format("\"%s\"", url2));
        log.debug("launchGame: Launched={} clients: rv={}", giid, url1);
        return url1;
    }

    /**
     * login to game server.
     * @param gamePlayer
     * @param request
     * @return "https://host:port/login?P=...,T=...,U=username,V=giid"
     */
    private String loginUrl(GamePlayer gamePlayer, HttpServletRequest request) {
        GameInst gameInst = gamePlayer.getGameInst();
        String token = getValidationToken(gamePlayer, request);
        String url = gameInst.getHostUrl() + "/login?" + token;
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
        String jsessions = getCookieValue("JSESSIONID", request);
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

    // @CheckNull
    private String getCookieValue(String name, HttpServletRequest req) {
        final Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) return cookie.getValue();
        }
        return null;
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

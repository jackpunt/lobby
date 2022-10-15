package com.thegraid.lobby.web.rest;

import com.thegraid.lobby.auth.NotGameException;
import com.thegraid.lobby.auth.TicketService.GameTicketService;
import com.thegraid.lobby.config.ApplicationProperties;
import com.thegraid.lobby.domain.Authority;
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
import gamma.main.GameLauncher;
import gamma.main.Launcher;
import gamma.main.Launcher.Game;
import gamma.main.Launcher.GameResults;
import gamma.main.Launcher.LaunchResults;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import liquibase.pro.packaged.g;
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
import org.springframework.web.client.RestTemplate;

@Primary
@RestController
@RequestMapping("gammaDS/gi")
public class GameInstResourceExt extends GameInstResource {

    // Using JWT: https://medium.com/innoventes/spring-boot-with-jwt-7970e5be4540

    static final Logger log = LoggerFactory.getLogger(GameInstResource.class);

    // using this local class until we build something to replace the InProcLauncher
    // and fix the protocol between gammaDS.GameInstController and gamma-web.GameLauncher
    // and fix the protocol between gammaJH.GameInstResourceExt and gamma-web-new.GameLauncher
    @Component("xgameLauncher")
    public static class NullGameLauncher implements GameLauncher.InProc {

        @Override
        public Game getLaunchedGame(Long giid) {
            return null;
        }

        @Override
        public void finished(GameResults results) {}

        @Override
        public LaunchResults launch(Long giid) {
            return null;
        }
    }

    /**
     * launch game using HTTP to gamma-web...LaunchService.
     */
    @Component("gameLauncher")
    public static class HttpGameLauncher implements GameLauncher.InProc {

        // TODO: do the right thing instead of ".InProc"
        // To be a Proxy to GameLauncher running on gamma[567].thegraid.com
        // using HTTP-REST:
        // Send PUT to start a new game
        // POST to 'start/terminate'? no real need from here. (client has ClockCtrl)
        // Send GET to get status
        // Send DEL to terminate?
        // Send message/request to game Server(giid) get actual status.

        @Value("${gamma.gameLaunchUrl}")
        private String gameLaunchUrl;

        @Autowired
        private ApplicationProperties props;

        @Override
        public Game getLaunchedGame(Long giid) {
            // GameDS: abort, pause, resume, setClockRate
            // Game: initialize, props, playerInfo
            return null; // If Game is 'launched' return handle to control it
        }

        @Override
        public void finished(GameResults results) {
            // properly done, this would notify the gameserver to kill the game
            // So: gamma-web Launcher would also implement InProc...
            // should provoke GameResults, with status to indicate invalid scores/ticks
        }

        /**
         * GammaDS stub that invokes launcher implementation.
         * Using HTTP/REST invocation (for load-balancing)
         * @return results {HostURL, StartTime}
         */
        @Override
        public LaunchResults launch(Long giid) {
            // https://spring.io/guides/gs/consuming-rest/
            // http://www.baeldung.com/rest-template
            RestTemplate rest = new RestTemplate();
            log.debug("launch: launchURL={}", gameLaunchUrl);
            //ResponseEntity<String> response = rest.getForEntity(launchURL + giid, String.class);
            LaunchResults results = rest.getForObject(gameLaunchUrl + giid, LaunchResults.Impl.class);
            return results;
        }
    }

    public static class RedirectDTO {

        final String url;

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

    /** GamePlayer.role value for PlayerA */
    public static final String ROLE_A = "A";

    /** GamePlayer.role value for PlayerB */
    public static final String ROLE_B = "B";

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

    public GamePlayer findGamePlayerInRole(GameInst gameInst, String role) {
        // GameInst & Player each have a Set of GamePlayers
        if (role == null) throw new IllegalArgumentException("Invalid role: " + role);
        List<GamePlayer> gamePlayers = gamePlayerRepository.getGamePlayers(gameInst);
        if (gamePlayers.isEmpty()) return null;
        for (GamePlayer gp : gamePlayers) {
            if (role.equals(gp.getRole())) return gp;
        }
        return null;
    }

    /** used for 'testing' with resetGiid
     * @return */
    private Authentication loginAs(User member) {
        Set<Authority> auths = member.getAuthorities();
        // confirm that given member has ROLE_USER
        if (
            !auths
                .stream()
                .anyMatch(a -> {
                    return a.getName().equals(AuthoritiesConstants.USER);
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
        GameLauncher.InProc gameLauncher2 = (GameLauncher.InProc) gameLauncher;
        Launcher.Game game = gameLauncher2.getLaunchedGame(giid);
        if (game != null) game.abort("resetGiid");
        gameLauncher2.finished(new Launcher.GameResults.Impl(giid)); // with null values...
        gameInst.setStarted(null);
        // could make: setResults(finished, scoreA, scoreB, ticks)
        gameInst.setFinished(null);
        gameInst.setScoreA(null);
        gameInst.setScoreB(null);
        gameInst.setTicks(null);
        gameInstRepository.save(gameInst); // reset fields in database.
        // Now: launchGameInstIfReady()
    }

    @RequestMapping(value = "redit/{role:[AB]}/{giid}") // TODO: redit/{giid}/{rold:[AB]}
    //@ResponseBody
    public RedirectDTO resetGiid(@PathVariable("role") String role, @PathVariable("giid") Long giid, HttpServletRequest request) {
        Optional<GameInst> gameInstOpt = gameInstRepository.findById(giid);
        if (gameInstOpt.isEmpty()) throw new NotGameException("Invalid gameInst id: " + giid);
        // resetGiid ONLY works when using the "InProc" GameLauncher!
        if (!(gameLauncher instanceof GameLauncher.InProc)) throw new IllegalStateException(
            "Not configured for resetGiid: " + gameLauncher
        );
        GameInst gameInst = gameInstOpt.get();
        // find User in given role:
        GamePlayer gamePlayer = findGamePlayerInRole(gameInst, role); // Assume URL identifies Player; spoof
        Player player = gamePlayer.getPlayer();
        User user = player.getUser();
        //User user = userService.getUserWithAuthorities().get();  // if User was actually logged-in
        log.debug("Player: {}, User: {}", player, user);
        Principal principal = (Principal) loginAs(user);
        if (principal == null) {
            throw new IllegalStateException("Could not login as " + user.getLogin());
        }
        log.debug("Principal requesting reset: {} of gameInst= {}", principal.getName(), gameInst);

        resetGame(gameInst);
        String otherRole = (role == ROLE_A) ? ROLE_B : ROLE_A;
        GamePlayer otherPlayer = findGamePlayerInRole(gameInst, otherRole);
        GameInstProps gameProps = gameInstPropsRepository.getReferenceById(gameInst.getId());

        String launchUrl = props.gamma.gameLaunchUrl; // where the launcher is listening
        String hostUrl = gameInst.getHostUrl(); // "https://game5.gamma.com:8445/launcher/GameControl/giid"
        String newUrl = replaceHostInUrl(hostUrl, launchUrl);
        log.info("temporary setLaunchUrl: {}", newUrl);
        props.gamma.gameLaunchUrl = newUrl;
        String url = launchGame(gamePlayer, otherPlayer, gameProps, request);
        props.gamma.gameLaunchUrl = launchUrl; // reset to original

        RedirectDTO redirect = new RedirectDTO(url);
        return redirect;
        // redirect to url (as returned form launchGame)
        //String format = "redirect:%1$s";
        // format = "<body>redirect:<a href=\"%1$s\">%1$s</a></body>"; // set
        // @ResponseBody above
        // url ="http://www.google.com";
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
    private String launchGame(GamePlayer gamePlayer1, GamePlayer gamePlayer2, GameInstProps gameProps, HttpServletRequest request) {
        GameInst gameInst = gamePlayer1.getGameInst();
        String base = request.getRequestURL().toString(); //baseURL(request);
        log.warn("launchGame: {} from {}", gameInst, base);
        // why synchronized here? maybe multiple client requests. ?SAME? Entity (no, entity is per-hibernate-session)
        // we are trusting that hibernate will 'sync' entity state across servers?
        synchronized (gameInst) {
            if (gameInst.getStarted() != null) {
                String url1 = loginUrl(gamePlayer1, request);
                log.warn("Game already started: {} @ {}", url1, gameInst.getStarted());
                return url1; // [re]start login & loading displayClient
            }
            log.warn("launchGame: gameLauncher={}", this.gameLauncher);

            // HttpInvoker-based Launcher; wait and parse the results into LaunchResults
            Launcher.LaunchResults results = gameLauncher.launch(gameInst.getId());
            Optional<GameInst> gameInstOpt = gameInstRepository.findById(gameInst.getId()); // try get NEW values
            gameInst = gameInstOpt.get(); // ASSERT: gameInstOpt.isPresent()
            log.debug("Launched gameInst: {}, results={}", gameInstOpt, results);

            if (results == null || results.getStarted() == null) {
                log.warn("Game launch failed: {} from {} results={}", gameInst, base, results);
                // TODO: something to provoke notification to the Member(s)' web page.
                String role = gamePlayer1.getRole();
                return editUrl(role, gameInst);
            }
        }
        // Game is launched, let players login (&start loading DisplayClient)
        String url1 = loginUrl(gamePlayer1, request);
        String url2 = loginUrl(gamePlayer2, request);
        log.debug("url1={}", url1);
        log.debug("url2={}", url2);
        // use GammaJMSBroadcaster.JSONP to send other member to login; (and then --> goActive?)
        // User member2 = gamePlayer2.getPlayer().getUser();
        // sendJsonp(member2, "events.nextpage", String.format("\"%s\"", url2));
        log.debug("launchGame: Launched={} clients: rv={}", gameInst, url1);
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
        String uuids = getCookieValue("JSESSIONID", request);
        String loginid = gamePlayer.getPlayer().getUser().getLogin();
        log.debug("getValidationToken: for {} JSESSIONID={}", loginid, uuids);
        // Jwt jwt = new Jwt();
        // JwtAuthenticationToken token = new JwtAuthenticationToken(jwt);
        Optional<User> optUser = userService.getUserWithAuthoritiesByLogin(loginid);
        if (optUser.isEmpty()) return null;
        User user = optUser.get();
        String username = user.getLogin();
        Long validTime = TimeUnit.HOURS.toMillis(3);
        Long gpid = gamePlayer.getId(); // @NotNull (was gamePlayer.getRole())
        String token = gameTicketService.getTicket(username, validTime, gpid, uuids); // includes U=loginId
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

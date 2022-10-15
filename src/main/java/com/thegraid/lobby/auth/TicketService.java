package com.thegraid.lobby.auth;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** common code with gammaJH and launcher. */
public class TicketService {

    protected static final Logger log = LoggerFactory.getLogger(TicketService.class);

    /**
     * create a time-limited, validate-able String, with labeled properties.
     * [implicit arg: username]
     * @param username asserts the username
     * @param salt either the user.salt OR salt(UUID=JSESSIONID) ?
     * @param validTime token is invalid after validTime
     * @param args "Key Value ..."
     * @return "P=signature,T=dddd,U=username,...""
     */
    // 2 cases: salt from JSESSIONID/UUID [EmailTicket] -or- salt from member.getSalt() [GameTicket]
    protected String getGenericTicket(String username, Long salt, long validTime, String... args) {
        // compose: ^P=(1)&(T=(ddd)&U=(username)&...)$
        // String username = user.getLogin();
        Long timelimit = new Date().getTime() + validTime;
        StringBuilder sb = new StringBuilder();
        sb.append("&T=").append(Long.toHexString(timelimit));
        sb.append("&U=").append(username);
        for (int ndx = 0; ndx < args.length; ndx += 2) {
            sb.append("&").append(args[ndx]).append("=").append(args[ndx + 1]);
        }
        String raw = sb.toString();
        String token = signature(raw, salt); // encode ticket with theSalt
        return "P=" + token + raw;
    }

    /** get Signature/hash for given text + salt. */
    private String signature(String text, Long salt) {
        String algo = "SHA-256"; // or SHA-512?
        try {
            MessageDigest digest = MessageDigest.getInstance(algo);
            byte[] text_bytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] salt_bytes = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(salt).array();
            byte[] hash = digest.digest(concatBytes(salt_bytes, text_bytes));
            String rv = Base64.getEncoder().encodeToString(hash);
            return rv;
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error: {}", ex);
            return ""; // only if algo not found
        }
    }

    private byte[] concatBytes(byte[] bytes1, byte[] bytes2) {
        int len = bytes1.length + bytes2.length;
        byte[] all_bytes = Arrays.copyOf(bytes1, len);
        System.arraycopy(bytes2, 0, all_bytes, len, bytes2.length);
        return all_bytes;
    }

    public static class Ticket {

        public boolean match = false;
        public String token; // @RequestParam("P")
        public String raw2; // (&T=...&U=...)
        public String tls; // @RequestParam("T")
        public String user; // @RequestParam("U")
        public String rest; // @RequestParam("V") or whatever

        protected Ticket(Pattern pat, CharSequence raw) {
            if (raw == null) return;
            Matcher matcher = pat.matcher(raw);
            match = matcher.matches();
            if (!match) return;
            token = matcher.group("P"); // P=encode(raw2,theSalt)
            raw2 = matcher.group("raw2"); // raw2=(&T=...&U=...)
            tls = matcher.group("T"); // T=expiryTime
            user = matcher.group("U"); // U=loginId [logged and validated in hash]
            rest = matcher.group("rest"); // possibly null or empty
        }
    }

    Boolean validateTicket(Pattern pat, CharSequence raw, Long salt) {
        log.trace("pat={}", pat);
        log.trace("raw={}", raw);
        Ticket ticket = new Ticket(pat, raw);
        if (!ticket.match) {
            log.debug("not a ticket: {}", raw.length());
            return null;
        }

        String hash = signature(ticket.raw2, salt);
        if (!hash.equals(ticket.token)) {
            log.trace(String.format("hash=%s,token=%s,raw2=%s\n", hash, ticket.token, ticket.raw2));
            log.debug("TicketService.validateTicket={}, user={}", "(corrupt)", ticket.user);
            return false; // corrupt data: throw new BadCredentialsException("") ?
        }
        long timelimit = Long.parseLong(ticket.tls, 16);
        if (new Date().getTime() > timelimit) {
            log.debug("TicketService.validateTicket={}, user={}", "(expired)", ticket.user);
            //throw new CredentialsExpiredException("User can try again with a new token");
            return false;
        }
        log.debug("TicketService.validateTicket={}, user={}", true, ticket.user);
        return true;
    }

    /** suitable for using JSESSIONID as a per-user/session Salt
     * @param uuids UUID as string of digits
     * @return Long hash of the bits of uuids
     */
    public Long getSaltFromUUID(String uuids) {
        String uuidss =
            uuids.substring(0, 7) +
            "-" +
            uuids.substring(8, 11) +
            "-" +
            uuids.substring(12, 15) +
            "-" +
            uuids.substring(16, 19) +
            "-" +
            uuids.substring(20);
        UUID uuid = UUID.fromString(uuidss); // all this to convert string to pair of long
        long uuid_salt = (uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits());
        log.trace("getSaltFomrUUID: {} --> {}", uuids, uuid_salt);
        return (Long) uuid_salt;
    }

    /** Validate Ticket using JSESSIONID as key. */
    @Service
    public static class GameTicketService extends TicketService {

        /** P=(\p{XDigit}{64})(&T=(\p{XDigit}{11,})&U=([^&]+)&V=(\d{1,19}))(&.*)?$ */
        private final Pattern pat = Pattern.compile(
            // "P=(\\p{XDigit}{40,})(&T=(\\p{XDigit}{11,})&U=([^&]+)&V=(\\d{1,19}))(&.*)?$");
            // "P=(\\p{XDigit}{64})(&T=(\\p{XDigit}{11,})&U=([^&]+)&V=(\\d{1,19}))(&.*)?$");
            "P=(?<P>\\p{XDigit}{64})(?<raw2>&T=(?<T>\\p{XDigit}{11,})&U=(?<U>[^&]+)&V=(\\d{1,19}))(?<rest>&.*)?$"
        );

        /**
         * Create query string Ticket for DisplayClient to authenticate to game server.
         *
         * @param username  include user.loginid
         * @param validTime deadlne for ticket to be valid
         * @param gpid      the long id (not null, not 0L)
         * @param uuids     String representing a UUID that is shared with the
         *                  GameServer.
         * @return Ticket signed with uuids
         */
        public String getTicket(String username, Long validTime, Long gpid, String uuids) {
            return getTicket(username, validTime, gpid, getSaltFromUUID(uuids));
        }

        // export this if there is some other salt to share with the validating side
        private String getTicket(String username, Long validTime, Long gpid, Long salt) {
            return super.getGenericTicket(username, salt, validTime, "V", gpid.toString());
        }

        public boolean validateTicket(CharSequence raw, Long salt) {
            return super.validateTicket(pat, raw, salt);
        }

        public Ticket parseTicket(String query) {
            return new Ticket(pat, query);
        }
    }
}

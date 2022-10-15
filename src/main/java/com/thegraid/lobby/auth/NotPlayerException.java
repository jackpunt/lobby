package com.thegraid.lobby.auth;

public class NotPlayerException extends IllegalArgumentException {

    private static final long serialVersionUID = 8331052656516145683L;

    public NotPlayerException() {
        super();
    }

    public NotPlayerException(String message) {
        super(message);
    }

    public NotPlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}

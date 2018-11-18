package com.futoshita.api.server.oauth.exception;

public class OAuth1UnauthorizedException extends RuntimeException {

    public OAuth1UnauthorizedException(String message) {
        super(message);
    }
}

package com.futoshita.jersey.server.service.exception;

public class NonUniqueException extends Exception {

    private static final long serialVersionUID = 1L;

    public NonUniqueException(String message) {
        super(message);
    }

}

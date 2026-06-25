package com.wipro.exception;

public class AuthenticationCredentialsNotFoundException extends RuntimeException {
    public AuthenticationCredentialsNotFoundException() {
        super();
    }

    public AuthenticationCredentialsNotFoundException(String message) {
        super(message);
    }
}

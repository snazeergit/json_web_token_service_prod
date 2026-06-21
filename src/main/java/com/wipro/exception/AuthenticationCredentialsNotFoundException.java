package com.wipro.exception;

public class AuthenticationCredentialsNotFoundException extends RuntimeException {
    public AuthenticationCredentialsNotFoundException(String message) {
        super(message);
    }
}
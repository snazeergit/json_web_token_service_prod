package com.wipro.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }

}

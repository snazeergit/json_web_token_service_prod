package com.wipro.exception;

public class TokenAlreadyRevokedException extends RuntimeException {
    public TokenAlreadyRevokedException() {
        super();
    }

    public TokenAlreadyRevokedException(String msg) {
        super(msg);
    }
}

package com.wipro.exception;

public class RefreshTokenReuseDetectedException extends RuntimeException {
    public RefreshTokenReuseDetectedException() {
        super();
    }

    public RefreshTokenReuseDetectedException(String msg) {
        super(msg);
    }
}

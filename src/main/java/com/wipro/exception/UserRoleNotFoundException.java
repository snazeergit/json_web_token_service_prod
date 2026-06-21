package com.wipro.exception;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException() {
        super();
    }

    public UserRoleNotFoundException(String message) {
        super(message);
    }
}

package com.wipro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
{
    "timestamp": "2026-06-22T02:21:15.604985",
    "status": 401,
    "error": "Unauthorized",
    "errorCode": "INVALID_REFRESH_TOKEN",
    "message": "Invalid refresh token",
    "path": "/api/auth/refresh"
}

Field               Purpose
------              ---------
timestamp      When the error occurred
status            HTTP status code
error               Standard HTTP error description
errorCode      Application-specific error code
message        Human-readable message
path                Endpoint that failed

 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
}

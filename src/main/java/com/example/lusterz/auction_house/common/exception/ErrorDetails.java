package com.example.lusterz.auction_house.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorDetails {
    
    private int status;
    private String error;
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ErrorDetails(int status, String error, String message, String details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}

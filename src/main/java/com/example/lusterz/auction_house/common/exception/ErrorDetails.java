package com.example.lusterz.auction_house.common.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorDetails {
    
    private int status;
    private String error;
    private String message;
    private String details;
    private String timestamp;

    public ErrorDetails(int status, String error, String message, String details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }
}

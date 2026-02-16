package com.example.lusterz.auction_house.common.exception;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorDetails {
    
    private String message;
    private String details;
    private LocalDateTime timestamp;

    public ErrorDetails(String message, String details) {
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}

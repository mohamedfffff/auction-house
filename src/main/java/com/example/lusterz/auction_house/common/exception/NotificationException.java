package com.example.lusterz.auction_house.common.exception;

public class NotificationException extends RuntimeException{
    public NotificationException(String message) {
        super(message);
    }

    public static class Email extends NotificationException{
        public Email(String message) {
            super(message);
        }
    }
}

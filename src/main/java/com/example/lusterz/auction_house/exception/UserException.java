package com.example.lusterz.auction_house.exception;

import com.example.lusterz.auction_house.exception.AuctionItemException.Unauthorized;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }

    public static class NotFound extends UserException {
        public NotFound(String message) {
            super(message);
        }

        public static NotFound byId(Long id) {
            return new NotFound("User not found with id " + id);
        }
        public static NotFound byUsername(String username) {
            return new NotFound("User not found with name " + username);
        }
    }

    public static class AlreadyExists extends UserException{
        public AlreadyExists(String message) {
            super(message);
        }

        public static AlreadyExists byEmail(String email) {
            return new AlreadyExists("Email " + email + " already exists");
        }
        public static AlreadyExists byUsername(String username) {
            return new AlreadyExists("Username " + username + " already exists");
        }
    }

    public static class Unauthorized extends UserException{
        public Unauthorized(String message) {
            super(message);
        }
        public static Unauthorized notOwner() {
            return new Unauthorized("User id does not match");
        }
    }

    public static class InsufficientFunds extends UserException{
        public InsufficientFunds(String message) {
            super(message);
        }

        public static InsufficientFunds lessThanBid(String bid) {
            return new InsufficientFunds("Balance is less than " + bid);
        }
    }
    
}

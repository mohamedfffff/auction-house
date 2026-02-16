package com.example.lusterz.auction_house.common.exception;

public class ItemException extends RuntimeException{
    public ItemException(String message) {
        super(message);
    }

    public static class NotFound extends ItemException{
        public NotFound(String message) {
            super(message);
        }

        public static NotFound byId(Long id) {
            return new NotFound("No auction item found with id " + id);
        }
    }

    public static class Unauthorized extends ItemException{
        public Unauthorized(String message) {
            super(message);
        }
        public static Unauthorized notOwner() {
            return new Unauthorized("User is not the owener of this item");
        }
    }

    public static class InvalidState extends ItemException{
        public InvalidState(String message) {
            super(message);
        }

        public static InvalidState hasBids() {
            return new InvalidState("Auction item has bids");
        }
        public static InvalidState notActive() {
            return new InvalidState("Auction hasn't started yet");
        }
        public static InvalidState alreadyStarted() {
            return new InvalidState("Auction has already started");
        }
        public static InvalidState invalidDuration() {
            return new InvalidState("Auction starting or ending date is invalid");
        }
    }
    
}

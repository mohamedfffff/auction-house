package com.example.lusterz.auction_house.exception;

import com.example.lusterz.auction_house.exception.AuctionItemException.Unauthorized;

public class BidException extends RuntimeException{
    public BidException(String message) {
        super(message);
    }

    public static class NotFound extends BidException{
        public NotFound(String message) {
            super(message);
        }

        public static NotFound byId(Long id) {
            return new NotFound("Bid not found with id " + id);
        }
    }

    public static class Unauthorized extends BidException{
        public Unauthorized(String message) {
            super(message);
        }
        public static Unauthorized notOwner() {
            return new Unauthorized("User is not the owener of the bid");
        }
        public static Unauthorized isOwner() {
            return new Unauthorized("Owner can't bid on his item");
        }
    }

    public static class InsufficientBid extends BidException{
        public InsufficientBid(String message) {
            super(message);
        }

        public static InsufficientBid lessThanHighest() {
            return new InsufficientBid("Bid is too low");
        }
    }
}

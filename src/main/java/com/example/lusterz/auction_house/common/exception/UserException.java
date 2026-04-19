package com.example.lusterz.auction_house.common.exception;

import java.math.BigDecimal;

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
        public static NotFound byEmail(String email) {
            return new NotFound("User not found with email " + email);
        }
        public static NotFound byIdentifier(String identifier) {
            return new NotFound(identifier + " user not");
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
        public static Unauthorized providerMismatch() {
            return new Unauthorized("User has no local credential");
        }
        public static Unauthorized notEnabled() {
            return new Unauthorized("User account is not enabled");
        }
    } 

    public static class PasswordMismatch extends UserException{
        public PasswordMismatch(String message) {
            super(message);
        }

        public static PasswordMismatch oldAndNew() {
            return new PasswordMismatch("Old password is incorrect");
        }
    }

    public static class InsufficientFunds extends UserException{
        public InsufficientFunds(String message) {
            super(message);
        }

        public static InsufficientFunds balanceLessThanBid(BigDecimal bid) {
            return new InsufficientFunds("Balance is less than " + bid);
        }
    }

    public static class AlreadyActive extends UserException{
        public AlreadyActive() {
            super("Account is already Active");
        }
    }

    public static class NotActive extends UserException{
        public NotActive() {
            super("Account is not Active");
        }
    }

     public static class NoCredentials extends UserException{
        public NoCredentials(String message) {
            super(message);
        }

        public static NoCredentials local() {
            return new NoCredentials("No local credentials found for user");
        }
        public static NoCredentials google() {
            return new NoCredentials("No google credentials found for user");
        }
    }

    public static class Credential extends AuthException{
        public Credential(String message) {
            super(message);
        }

        public static Credential noLocal() {
            return new Credential("User doesn't have a local credentials");
        }

        public static Credential hasLocal() {
            return new Credential("User already has a local credentials");
        }

        public static Credential noOauth2() {
            return new Credential("User doesn't have an oauth2 credentials");
        }

        public static Credential hasOauth2() {
            return new Credential("User already has an oauth2 credentials");
        }
    }
    
}

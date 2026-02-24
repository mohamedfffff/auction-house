package com.example.lusterz.auction_house.common.exception;


public class AuthException extends RuntimeException{
    public AuthException(String message) {
        super(message);
    }

    public static class JwtError extends AuthException{
        public JwtError(String message) {
            super(message);
        }

        public static JwtError malformed() {
            return new JwtError("Invalid token format");
        }
        public static JwtError invalidSignature() {
            return new JwtError("Security Signature mismatch");
        }
        public static JwtError expired() {
            return new JwtError("Session expired. Please login again");
        }
    }

    public static class Unauthorized extends AuthException{
        public Unauthorized(String message) {
            super(message);
        }

        public static Unauthorized wrongPassword() {
            return new Unauthorized("The password is incorrect");
        }
    }
}

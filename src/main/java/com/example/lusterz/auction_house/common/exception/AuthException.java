package com.example.lusterz.auction_house.common.exception;


public class AuthException extends RuntimeException{
    public AuthException(String message) {
        super(message);
    }

    public static class JwtToken extends AuthException{
        public JwtToken(String message) {
            super(message);
        }

        public static JwtToken malformed() {
            return new JwtToken("Invalid token format");
        }
        public static JwtToken invalidSignature() {
            return new JwtToken("Security Signature mismatch");
        }
        public static JwtToken expired() {
            return new JwtToken("Token expired");
        }
    }

    public static class RefreshToken extends AuthException{
        public RefreshToken(String message) {
            super(message);
        }

        public static RefreshToken expired() {
            return new RefreshToken("Session expired. Please login again");
        }
        public static RefreshToken notFound() {
            return new RefreshToken("Refresh token not found");
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

    public static class Provider extends AuthException{
        public Provider(String message) {
            super(message);
        }

        public static Provider notLocal() {
            return new Provider("User doesn't have a local password");
        }

        public static Provider noEmail() {
            return new Provider("The email isn't provided by provider");
        }
    }
}

package com.example.textsocial.social.api;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() { super("Invalid username or password"); }
}

package com.example.textsocial.social.api;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String username) { super("Username is already taken: " + username); }
}


package com.example.textsocial.social.api;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource) { super(resource + " not found"); }
}

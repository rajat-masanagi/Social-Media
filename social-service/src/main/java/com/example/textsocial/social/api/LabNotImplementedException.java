package com.example.textsocial.social.api;

public class LabNotImplementedException extends RuntimeException {
    public LabNotImplementedException(String lab, String task) {
        super(lab + ": " + task);
    }
}


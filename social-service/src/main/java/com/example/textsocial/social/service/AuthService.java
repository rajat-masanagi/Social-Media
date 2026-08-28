package com.example.textsocial.social.service;

import com.example.textsocial.social.api.LabNotImplementedException;
import com.example.textsocial.social.api.SocialDtos.AuthResponse;
import com.example.textsocial.social.api.SocialDtos.LoginRequest;
import com.example.textsocial.social.api.SocialDtos.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public AuthResponse register(RegisterRequest request) {
        // TODO(LAB-2): normalize username, hash password, save user, then issue a JWT.
        throw new LabNotImplementedException("LAB-2", "Implement registration in AuthService");
    }
    public AuthResponse login(LoginRequest request) {
        // TODO(LAB-2): look up user, verify BCrypt hash, and issue a JWT.
        throw new LabNotImplementedException("LAB-2", "Implement login in AuthService");
    }
}


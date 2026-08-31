package com.example.textsocial.social.service;

import com.example.textsocial.social.api.AuthenticationException;
import com.example.textsocial.social.api.SocialDtos.AuthResponse;
import com.example.textsocial.social.api.SocialDtos.LoginRequest;
import com.example.textsocial.social.api.SocialDtos.RegisterRequest;
import com.example.textsocial.social.api.SocialDtos.UserResponse;
import com.example.textsocial.social.api.UsernameTakenException;
import com.example.textsocial.social.domain.UserAccount;
import com.example.textsocial.social.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder passwords, JwtService jwt) {
        this.users = users;
        this.passwords = passwords;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim().toLowerCase(java.util.Locale.ROOT);
        if (users.existsByUsername(username)) throw new UsernameTakenException(username);
        UserAccount user;
        try {
            user = users.saveAndFlush(new UserAccount(
                    UUID.randomUUID(), username, passwords.encode(request.password()), Instant.now()));
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameTakenException(username);
        }
        return new AuthResponse(jwt.issue(user), jwt.ttlSeconds(),
                new UserResponse(user.getId(), user.getUsername(), user.getFollowerCount(), false));
    }
    public AuthResponse login(LoginRequest request) {
        String username = request.username().trim().toLowerCase(java.util.Locale.ROOT);
        UserAccount user = users.findByUsername(username)
                .orElseThrow(AuthenticationException::new);
        if (!passwords.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException();
        }
        return new AuthResponse(jwt.issue(user), jwt.ttlSeconds(),
                new UserResponse(user.getId(), user.getUsername(), user.getFollowerCount(), false));
    }
}

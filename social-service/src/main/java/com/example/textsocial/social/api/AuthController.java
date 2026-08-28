package com.example.textsocial.social.api;

import static com.example.textsocial.social.api.SocialDtos.*;

import com.example.textsocial.social.service.AuthService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }
    @PostMapping("/api/auth/register") AuthResponse register(@Valid @RequestBody RegisterRequest request) { return auth.register(request); }
    @PostMapping("/api/auth/login") AuthResponse login(@Valid @RequestBody LoginRequest request) { return auth.login(request); }
    @GetMapping("/api/me") UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        return new UserResponse(UUID.fromString(jwt.getSubject()), jwt.getClaimAsString("username"), 0, false);
    }
}


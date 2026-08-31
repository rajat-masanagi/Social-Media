package com.example.textsocial.social.api;

import static com.example.textsocial.social.api.SocialDtos.*;

import com.example.textsocial.social.service.AuthService;
import com.example.textsocial.social.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthService auth;
    private final UserRepository users;
    public AuthController(AuthService auth, UserRepository users) { this.auth = auth; this.users = users; }
    @PostMapping("/api/auth/register") AuthResponse register(@Valid @RequestBody RegisterRequest request) { return auth.register(request); }
    @PostMapping("/api/auth/login") AuthResponse login(@Valid @RequestBody LoginRequest request) { return auth.login(request); }
    @GetMapping("/api/me") UserResponse me(@AuthenticationPrincipal Jwt jwt) {
        var user = users.findById(UUID.fromString(jwt.getSubject())).orElseThrow();
        return new UserResponse(user.getId(), user.getUsername(), user.getFollowerCount(), false);
    }
}

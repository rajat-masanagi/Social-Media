package com.example.textsocial.social.api;

import static com.example.textsocial.social.api.SocialDtos.*;

import com.example.textsocial.social.service.SocialService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SocialController {
    private final SocialService social;
    public SocialController(SocialService social) { this.social = social; }

    @GetMapping("/users/{username}") UserResponse user(@PathVariable("username") String username, @AuthenticationPrincipal Jwt jwt) { return social.user(username, userId(jwt)); }
    @PostMapping("/users/{id}/follow") ResponseEntity<Void> follow(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) { social.follow(id, userId(jwt)); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/users/{id}/follow") ResponseEntity<Void> unfollow(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) { social.unfollow(id, userId(jwt)); return ResponseEntity.noContent().build(); }
    @PostMapping("/posts") ContentResponse post(@Valid @RequestBody CreateContentRequest body, @AuthenticationPrincipal Jwt jwt) { return social.createPost(body, userId(jwt)); }
    @GetMapping("/posts/{id}") ContentResponse post(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) { return social.content(id, userId(jwt)); }
    @PostMapping("/posts/{id}/replies") ContentResponse reply(@PathVariable("id") UUID id, @Valid @RequestBody CreateContentRequest body, @AuthenticationPrincipal Jwt jwt) { return social.reply(id, body, userId(jwt)); }
    @GetMapping("/posts/{id}/replies") PageResponse<ContentResponse> replies(@PathVariable("id") UUID id, @RequestParam(value = "cursor", required = false) String cursor, @RequestParam(value = "limit", defaultValue = "20") int limit, @AuthenticationPrincipal Jwt jwt) { return social.replies(id, cursor, Math.min(limit, 20), userId(jwt)); }
    @PostMapping("/posts/{id}/likes/me") ResponseEntity<Void> like(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) { social.like(id, userId(jwt)); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/posts/{id}/likes/me") ResponseEntity<Void> unlike(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) { social.unlike(id, userId(jwt)); return ResponseEntity.noContent().build(); }

    private UUID userId(Jwt jwt) { return jwt == null ? null : UUID.fromString(jwt.getSubject()); }
}

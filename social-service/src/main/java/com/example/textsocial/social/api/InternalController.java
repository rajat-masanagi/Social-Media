package com.example.textsocial.social.api;

import static com.example.textsocial.social.api.SocialDtos.*;

import com.example.textsocial.social.service.SocialService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal")
class InternalController {
    private final SocialService social;
    private final String expectedKey;
    InternalController(SocialService social, @Value("${app.internal-api-key}") String expectedKey) { this.social = social; this.expectedKey = expectedKey; }
    @GetMapping("/users/{id}/followers") FollowerPage followers(@RequestHeader("X-Internal-Key") String key, @PathVariable UUID id, @RequestParam(required = false) String cursor, @RequestParam(defaultValue = "100") int limit) { check(key); return social.followers(id, cursor, Math.min(limit, 100)); }
    @GetMapping("/users/{id}/following/celebrities") List<UUID> celebrities(@RequestHeader("X-Internal-Key") String key, @PathVariable UUID id) { check(key); return social.followedCelebrities(id); }
    @PostMapping("/posts/batch") List<ContentResponse> batch(@RequestHeader("X-Internal-Key") String key, @RequestBody BatchContentRequest request) { check(key); return social.batch(request); }
    private void check(String key) { if (!expectedKey.equals(key)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); }
}


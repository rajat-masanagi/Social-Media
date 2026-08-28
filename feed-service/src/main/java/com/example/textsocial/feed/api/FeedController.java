package com.example.textsocial.feed.api;

import com.example.textsocial.feed.service.FeedService;
import com.example.textsocial.feed.service.FeedService.FeedPage;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
class FeedController {
    private final FeedService feed;
    FeedController(FeedService feed) { this.feed = feed; }
    @GetMapping FeedPage feed(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) String cursor,
                              @RequestParam(defaultValue = "20") int limit) {
        return feed.feed(UUID.fromString(jwt.getSubject()), cursor, Math.min(limit, 20));
    }
}


package com.example.textsocial.feed.client;

import com.example.textsocial.feed.config.FeignConfig;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "social-internal", url = "${app.social-url}", configuration = FeignConfig.class)
public interface SocialInternalClient {
    @GetMapping("/internal/users/{id}/followers") FollowerPage followers(@PathVariable UUID id, @RequestParam String cursor, @RequestParam int limit);
    @GetMapping("/internal/users/{id}/following/celebrities") List<UUID> celebrities(@PathVariable UUID id);
    @PostMapping("/internal/posts/batch") List<ContentView> batch(@RequestBody BatchRequest request);
    record FollowerPage(List<UUID> followerIds, String nextCursor) {}
    record BatchRequest(List<UUID> ids, UUID viewerId) {}
    record ContentView(UUID id, UUID rootId, UUID parentId, UUID authorId, String authorUsername,
                       String text, Instant createdAt, long replyCount, long likeCount, boolean likedByMe) {}
}


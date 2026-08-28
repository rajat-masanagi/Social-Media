package com.example.textsocial.social.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class SocialDtos {
    private SocialDtos() {}

    public record RegisterRequest(
            @NotBlank @Pattern(regexp = "[a-z0-9_]{3,30}") String username,
            @NotBlank @Size(min = 8, max = 72) String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String accessToken, long expiresInSeconds, UserResponse user) {}
    public record UserResponse(UUID id, String username, long followerCount, boolean followedByMe) {}
    public record CreateContentRequest(@NotBlank @Size(max = 250) String text) {}
    public record ContentResponse(UUID id, UUID rootId, UUID parentId, UUID authorId,
                                  String authorUsername, String text, Instant createdAt,
                                  long replyCount, long likeCount, boolean likedByMe) {}
    public record PageResponse<T>(List<T> items, String nextCursor) {}
    public record BatchContentRequest(@Size(max = 20) List<UUID> ids, UUID viewerId) {}
    public record FollowerPage(List<UUID> followerIds, String nextCursor) {}
}


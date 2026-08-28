package com.example.textsocial.social.service;

import com.example.textsocial.social.api.LabNotImplementedException;
import com.example.textsocial.social.api.SocialDtos.*;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SocialService {
    public UserResponse user(String username, UUID viewerId) { throw todo(); }
    public void follow(UUID targetId, UUID viewerId) { throw todo(); }
    public void unfollow(UUID targetId, UUID viewerId) { throw todo(); }
    public ContentResponse createPost(CreateContentRequest request, UUID authorId) { throw todo(); }
    public ContentResponse reply(UUID parentId, CreateContentRequest request, UUID authorId) { throw todo(); }
    public ContentResponse content(UUID id, UUID viewerId) { throw todo(); }
    public PageResponse<ContentResponse> replies(UUID parentId, String cursor, int limit, UUID viewerId) { throw todo(); }
    public void like(UUID contentId, UUID viewerId) { throw todo(); }
    public void unlike(UUID contentId, UUID viewerId) { throw todo(); }
    public FollowerPage followers(UUID authorId, String cursor, int limit) { throw todo(); }
    public List<UUID> followedCelebrities(UUID userId) { throw todo(); }
    public List<ContentResponse> batch(BatchContentRequest request) { throw todo(); }
    private LabNotImplementedException todo() {
        // TODO(LAB-3): implement transactional content, follows, likes, cursors, and internal projections.
        return new LabNotImplementedException("LAB-3", "Implement this SocialService use case");
    }
}


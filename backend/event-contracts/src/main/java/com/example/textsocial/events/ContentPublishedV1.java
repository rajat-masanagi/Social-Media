package com.example.textsocial.events;

import java.time.Instant;
import java.util.UUID;

/** Stable wire contract. Add fields compatibly; create V2 for breaking changes. */
public record ContentPublishedV1(
        UUID eventId,
        UUID contentId,
        UUID rootId,
        UUID parentId,
        UUID authorId,
        String authorUsername,
        String text,
        Instant createdAt,
        ContentType contentType,
        boolean celebrityAuthor
) {
    public enum ContentType { POST, REPLY }
}


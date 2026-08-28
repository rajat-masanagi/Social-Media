package com.example.textsocial.social.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "content")
public class Content {
    @Id private UUID id;
    @Column(name = "root_id", nullable = false) private UUID rootId;
    @Column(name = "parent_id") private UUID parentId;
    @Column(name = "author_id", nullable = false) private UUID authorId;
    @Column(nullable = false, length = 250) private String text;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected Content() {}
    public UUID getId() { return id; }
    public UUID getRootId() { return rootId; }
    public UUID getParentId() { return parentId; }
    public UUID getAuthorId() { return authorId; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
}


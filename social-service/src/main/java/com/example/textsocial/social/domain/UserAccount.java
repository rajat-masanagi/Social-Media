package com.example.textsocial.social.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 30) private String username;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Column(name = "follower_count", nullable = false) private long followerCount;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected UserAccount() {}
    public UserAccount(UUID id, String username, String passwordHash, Instant createdAt) {
        this.id = id; this.username = username; this.passwordHash = passwordHash; this.createdAt = createdAt;
    }
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public long getFollowerCount() { return followerCount; }
    public Instant getCreatedAt() { return createdAt; }
}


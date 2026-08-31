CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    follower_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE follows (
    follower_id BINARY(16) NOT NULL,
    followed_id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (follower_id, followed_id),
    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES users(id),
    CONSTRAINT fk_follow_followed FOREIGN KEY (followed_id) REFERENCES users(id),
    INDEX idx_follows_followed (followed_id, follower_id)
);

CREATE TABLE content (
    id BINARY(16) PRIMARY KEY,
    root_id BINARY(16) NOT NULL,
    parent_id BINARY(16) NULL,
    author_id BINARY(16) NOT NULL,
    text VARCHAR(250) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_content_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_content_parent FOREIGN KEY (parent_id) REFERENCES content(id),
    INDEX idx_content_parent_time (parent_id, created_at, id),
    INDEX idx_content_author_time (author_id, created_at, id)
);

CREATE TABLE likes (
    user_id BINARY(16) NOT NULL,
    content_id BINARY(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (user_id, content_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_like_content FOREIGN KEY (content_id) REFERENCES content(id),
    INDEX idx_likes_content (content_id)
);

CREATE TABLE outbox_events (
    id BINARY(16) PRIMARY KEY,
    aggregate_id BINARY(16) NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    payload JSON NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    published_at TIMESTAMP(6) NULL,
    INDEX idx_outbox_unpublished (published_at, created_at)
);


package com.example.textsocial.feed.domain;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("author_posts_by_author_day")
public class AuthorPost {
    @PrimaryKey private AuthorPostKey key;
    public AuthorPost() {}
    public AuthorPost(AuthorPostKey key) { this.key=key; }
    public AuthorPostKey getKey() { return key; }
}

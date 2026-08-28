package com.example.textsocial.feed.domain;

import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("home_feed_by_user_day")
public class TimelineEntry {
    @PrimaryKey private TimelineKey key;
    @Column("author_id") private UUID authorId;
    public TimelineEntry() {}
    public TimelineKey getKey() { return key; }
    public UUID getAuthorId() { return authorId; }
}


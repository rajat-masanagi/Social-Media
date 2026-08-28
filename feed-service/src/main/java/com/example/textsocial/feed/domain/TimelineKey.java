package com.example.textsocial.feed.domain;

import static org.springframework.data.cassandra.core.cql.Ordering.DESCENDING;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class TimelineKey implements Serializable {
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PARTITIONED) private UUID userId;
    @PrimaryKeyColumn(name = "bucket_day", ordinal = 1, type = PARTITIONED) private LocalDate bucketDay;
    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = CLUSTERED, ordering = DESCENDING) private Instant createdAt;
    @PrimaryKeyColumn(name = "post_id", ordinal = 3, type = CLUSTERED) private UUID postId;
    public TimelineKey() {}
    public UUID getUserId() { return userId; }
    public LocalDate getBucketDay() { return bucketDay; }
    public Instant getCreatedAt() { return createdAt; }
    public UUID getPostId() { return postId; }
}

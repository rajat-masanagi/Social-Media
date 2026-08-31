package com.example.textsocial.feed.repository;

import com.example.textsocial.feed.domain.TimelineEntry;
import com.example.textsocial.feed.domain.TimelineKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.time.LocalDate; import java.time.Instant; import java.util.List; import java.util.UUID;
import org.springframework.data.cassandra.repository.Query;

public interface TimelineRepository extends CassandraRepository<TimelineEntry, TimelineKey> {
    @Query("SELECT * FROM home_feed_by_user_day WHERE user_id=?0 AND bucket_day=?1")
    List<TimelineEntry> findBucket(UUID userId, LocalDate day);
}

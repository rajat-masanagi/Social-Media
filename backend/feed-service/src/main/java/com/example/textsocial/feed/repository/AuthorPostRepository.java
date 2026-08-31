package com.example.textsocial.feed.repository;

import com.example.textsocial.feed.domain.AuthorPost;
import com.example.textsocial.feed.domain.AuthorPostKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.time.LocalDate; import java.util.List; import java.util.UUID;
import org.springframework.data.cassandra.repository.Query;

public interface AuthorPostRepository extends CassandraRepository<AuthorPost, AuthorPostKey> {
    @Query("SELECT * FROM author_posts_by_author_day WHERE author_id=?0 AND bucket_day=?1")
    List<AuthorPost> findBucket(UUID authorId, LocalDate day);
}

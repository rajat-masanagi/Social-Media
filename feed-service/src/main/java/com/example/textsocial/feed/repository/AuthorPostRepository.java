package com.example.textsocial.feed.repository;

import com.example.textsocial.feed.domain.AuthorPost;
import com.example.textsocial.feed.domain.AuthorPostKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface AuthorPostRepository extends CassandraRepository<AuthorPost, AuthorPostKey> {}


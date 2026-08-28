package com.example.textsocial.feed.repository;

import com.example.textsocial.feed.domain.TimelineEntry;
import com.example.textsocial.feed.domain.TimelineKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TimelineRepository extends CassandraRepository<TimelineEntry, TimelineKey> {}


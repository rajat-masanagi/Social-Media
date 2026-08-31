package com.example.textsocial.search.repository;

import com.example.textsocial.search.domain.ContentDocument;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ContentSearchRepository extends ElasticsearchRepository<ContentDocument, UUID> {}


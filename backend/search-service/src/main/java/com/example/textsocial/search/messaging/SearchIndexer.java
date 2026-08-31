package com.example.textsocial.search.messaging;

import com.example.textsocial.events.ContentPublishedV1;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.textsocial.search.domain.ContentDocument;
import com.example.textsocial.search.repository.ContentSearchRepository;

@Component
public class SearchIndexer {
    private final ContentSearchRepository repository;
    public SearchIndexer(ContentSearchRepository repository) { this.repository = repository; }
    @KafkaListener(topics = "${app.kafka.content-topic}", groupId = "search-service",
            autoStartup = "${app.kafka.listener-enabled:false}")
    public void index(ContentPublishedV1 event) {
        repository.save(new ContentDocument(event.contentId(), event.rootId(), event.parentId(), event.authorId(),
                event.authorUsername(), event.text(), event.createdAt(), event.contentType().name()));
    }
}

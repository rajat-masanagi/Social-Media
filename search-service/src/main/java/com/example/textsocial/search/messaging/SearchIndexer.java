package com.example.textsocial.search.messaging;

import com.example.textsocial.events.ContentPublishedV1;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchIndexer {
    @KafkaListener(topics = "${app.kafka.content-topic}", groupId = "search-service",
            autoStartup = "${app.kafka.listener-enabled:false}")
    public void index(ContentPublishedV1 event) {
        // TODO(LAB-5): map the event to ContentDocument and save by content ID (idempotent upsert).
    }
}


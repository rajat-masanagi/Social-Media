package com.example.textsocial.feed.messaging;

import com.example.textsocial.events.ContentPublishedV1;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContentPublishedConsumer {
    @KafkaListener(topics = "${app.kafka.content-topic}", groupId = "feed-service",
            autoStartup = "${app.kafka.listener-enabled:false}")
    public void consume(ContentPublishedV1 event) {
        // TODO(LAB-6): ignore replies; always write author timeline; fan out only non-celebrity posts.
    }
}


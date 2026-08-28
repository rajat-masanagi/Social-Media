package com.example.textsocial.social.messaging;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisher {
    @Scheduled(fixedDelayString = "${app.outbox.poll-ms:1000}")
    void publishBatch() {
        // TODO(LAB-4): lock unpublished rows, publish ContentPublishedV1, then mark them published.
        // Keep the DB transaction short and assume Kafka delivery is at least once.
    }
}


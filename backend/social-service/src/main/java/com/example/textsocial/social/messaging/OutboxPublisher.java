package com.example.textsocial.social.messaging;

import com.example.textsocial.events.ContentPublishedV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private final JdbcTemplate jdbc; private final KafkaTemplate<String, ContentPublishedV1> kafka; private final ObjectMapper mapper; private final String topic; private final int batchSize;
    public OutboxPublisher(JdbcTemplate jdbc, KafkaTemplate<String, ContentPublishedV1> kafka, ObjectMapper mapper,
                           @Value("${app.kafka.content-topic:content-published-v1}") String topic,
                           @Value("${app.outbox.batch-size:50}") int batchSize) { this.jdbc=jdbc; this.kafka=kafka; this.mapper=mapper; this.topic=topic; this.batchSize=batchSize; }
    @Scheduled(fixedDelayString = "${app.outbox.poll-ms:1000}")
    @Transactional
    public void publishBatch() {
        List<MapRow> rows=jdbc.query("SELECT BIN_TO_UUID(id),BIN_TO_UUID(aggregate_id),payload FROM outbox_events WHERE published_at IS NULL ORDER BY created_at LIMIT ? FOR UPDATE SKIP LOCKED",(rs,n)->new MapRow(UUID.fromString(rs.getString(1)),UUID.fromString(rs.getString(2)),rs.getString(3)),batchSize);
        for(MapRow row:rows){ try { ContentPublishedV1 event=mapper.readValue(row.payload,ContentPublishedV1.class); kafka.send(topic,row.aggregate.toString(),event).get(10,TimeUnit.SECONDS); jdbc.update("UPDATE outbox_events SET published_at=UTC_TIMESTAMP(6) WHERE id=UUID_TO_BIN(?)",row.id.toString()); } catch(InterruptedException ex){ Thread.currentThread().interrupt(); throw new IllegalStateException("Outbox publish interrupted",ex); } catch(Exception ex){ throw new IllegalStateException("Outbox publish failed",ex); } }
    }
    private record MapRow(UUID id, UUID aggregate, String payload) {}
}

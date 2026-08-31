package com.example.textsocial.feed.messaging;

import com.example.textsocial.events.ContentPublishedV1;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.textsocial.feed.repository.*;
import com.example.textsocial.feed.domain.*;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class ContentPublishedConsumer {
    private final AuthorPostRepository authors; private final TimelineRepository timelines; private final com.example.textsocial.feed.client.SocialInternalClient social;
    public ContentPublishedConsumer(AuthorPostRepository authors, TimelineRepository timelines, com.example.textsocial.feed.client.SocialInternalClient social) { this.authors=authors; this.timelines=timelines; this.social=social; }
    @KafkaListener(topics = "${app.kafka.content-topic}", groupId = "feed-service",
            autoStartup = "${app.kafka.listener-enabled:false}")
    public void consume(ContentPublishedV1 event) {
        if (event.contentType() != ContentPublishedV1.ContentType.POST) return;
        authors.save(new AuthorPost(new AuthorPostKey(event.authorId(), LocalDate.ofInstant(event.createdAt(), java.time.ZoneOffset.UTC), event.createdAt(), event.contentId())));
        if (!event.celebrityAuthor()) {
            String cursor = null;
            do {
                var page = social.followers(event.authorId(), cursor, 100);
                for (UUID follower : page.followerIds()) {
                    timelines.save(new TimelineEntry(new TimelineKey(follower, LocalDate.ofInstant(event.createdAt(), java.time.ZoneOffset.UTC), event.createdAt(), event.contentId()), event.authorId()));
                }
                cursor = page.nextCursor();
            } while (cursor != null);
        }
    }
}

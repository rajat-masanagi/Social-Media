package com.example.textsocial.feed.service;

import com.example.textsocial.feed.client.SocialInternalClient.ContentView;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FeedService {
    public FeedPage feed(UUID userId, String cursor, int limit) {
        // TODO(LAB-6): read normal feed partitions, pull celebrity author partitions,
        // merge/dedupe/sort IDs, then hydrate them with one Social Service batch call.
        throw new LabNotImplementedException("LAB-6: implement hybrid feed reads");
    }
    public record FeedPage(List<ContentView> items, String nextCursor) {}
    public static class LabNotImplementedException extends RuntimeException {
        public LabNotImplementedException(String message) { super(message); }
    }
}


package com.example.textsocial.search.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    public SearchPage search(String query, String cursor, int limit) {
        // TODO(LAB-5): use ElasticsearchOperations with match query + search_after cursor.
        throw new LabNotImplementedException("LAB-5: implement full-text search");
    }
    public record SearchItem(UUID id, UUID rootId, String authorUsername, String text, Instant createdAt, String contentType) {}
    public record SearchPage(List<SearchItem> items, String nextCursor) {}
    public static class LabNotImplementedException extends RuntimeException {
        public LabNotImplementedException(String message) { super(message); }
    }
}


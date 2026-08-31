package com.example.textsocial.search.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import com.example.textsocial.search.domain.ContentDocument;
import com.example.textsocial.search.repository.ContentSearchRepository;

@Service
public class SearchService {
    private final ContentSearchRepository repository;
    public SearchService(ContentSearchRepository repository) { this.repository = repository; }
    public SearchPage search(String query, String cursor, int limit) {
        String term = query.trim().toLowerCase();
        int offset = cursor == null ? 0 : Integer.parseInt(cursor);
        List<SearchItem> all = StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(d -> (d.getText() != null && d.getText().toLowerCase().contains(term)) ||
                             (d.getAuthorUsername() != null && d.getAuthorUsername().toLowerCase().contains(term)))
                .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(d -> new SearchItem(d.getId(), d.getRootId(), d.getAuthorUsername(), d.getText(), d.getCreatedAt(), d.getContentType()))
                .toList();
        int end = Math.min(offset + Math.max(1, Math.min(limit, 20)), all.size());
        return new SearchPage(all.subList(Math.min(offset, all.size()), end), end < all.size() ? String.valueOf(end) : null);
    }
    public record SearchItem(UUID id, UUID rootId, String authorUsername, String text, Instant createdAt, String contentType) {}
    public record SearchPage(List<SearchItem> items, String nextCursor) {}
}

package com.example.textsocial.search.api;

import com.example.textsocial.search.service.SearchService;
import com.example.textsocial.search.service.SearchService.SearchPage;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/search")
class SearchController {
    private final SearchService search;
    SearchController(SearchService search) { this.search = search; }
    @GetMapping SearchPage search(@RequestParam("q") @Size(min = 2, max = 250) String q,
                                  @RequestParam(value = "cursor", required = false) String cursor,
                                  @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return search.search(q, cursor, Math.min(limit, 20));
    }
}

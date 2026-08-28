# Ordered labs

Search the repository with `rg "TODO\(LAB-"`. Keep `mvn test` green after every small change.

## Lab 1 — Trace the empty system

- Start infrastructure and all applications.
- Call each `/actuator/health` endpoint directly and through the gateway where applicable.
- Follow a request from React's `api.js` to a controller, service, and repository boundary.
- Acceptance: all projects build; unfinished application routes return a clear 501 response.

## Lab 2 — Authentication

- Implement `AuthService.register` and `login` with `UserRepository`, BCrypt, and `JwtService`.
- Normalize usernames before lookup and translate a duplicate key into HTTP 409.
- Add tests for duplicate/case-variant usernames, password boundaries, bad credentials, valid JWT, and expired JWT.
- Acceptance: register returns a one-hour token; login with the same credentials works; `/api/me` rejects missing/invalid tokens.

## Lab 3 — Social model

- Implement `SocialService` one method at a time using transactions and repository/JdbcTemplate queries.
- A reply must point to existing content and inherit its root ID. Use `(created_at,id)` opaque cursors for direct children.
- Make follow/like inserts and deletes idempotent. Atomically maintain follower count and forbid self-follow.
- Insert `ContentPublishedV1` JSON into the outbox in the same transaction as a post/reply.
- Acceptance: all public endpoints in `requests.http` work, including nested reply expansion and 250-character validation.

## Lab 4 — Transactional outbox

- Implement `OutboxPublisher` in batches. Prevent concurrent pollers claiming the same rows.
- Publish with content ID as Kafka key to preserve per-content/author ordering choices.
- Mark published only after broker acknowledgement; leave failed rows retryable.
- Acceptance: rolling back content leaves no outbox event; a simulated post-publish crash can duplicate an event without corrupting consumers.

## Lab 5 — Search projection

- Enable the Search listener and configure JSON deserialization for `ContentPublishedV1`.
- Implement idempotent event-to-document mapping and `SearchService` with match query, newest-first tie-breaking, and `search_after` cursor.
- Add an Elasticsearch integration test, allowing for refresh/eventual consistency.
- Acceptance: posts and replies become searchable; replaying one event does not create another document.

## Lab 6 — Feed and celebrity fan-out

- Enable the Feed listener and write every top-level post to the author-day table.
- For ordinary authors, page through followers and write user-day rows. Do not fan out replies or celebrity posts.
- Implement backward bucket reads, celebrity pulls, merge/deduplication, cursor encoding, and one batch hydration call.
- Add tests for chronological merge, duplicate delivery, threshold behavior, max page size, and Social Service failure.
- Acceptance: a normal post creates follower rows; a celebrity post does not; both appear in the reader's timeline.

## Lab 7 — Finish React wiring

- The screens and API calls already exist. Add loading/empty states and refresh affected state after mutations.
- Add a like button to `PostCard` and cursor “load more” controls.
- Keep recursive reply fetching lazy; never request the entire tree automatically.
- Acceptance: register, login, post, follow, feed, reply, like, and search work from the browser without direct service URLs.

## Lab 8 — Measure, then explain

- Install JMeter 5.6.3 and set `JMETER_HOME`.
- Run 1, 10, and 50 users for two minutes, recording throughput, errors, p50, p95, and p99.
- Create an author with 99 followers and compare their fan-out work with the same author at 100 followers.
- Stop Social Service during a feed test and record the failure behavior.
- Acceptance: save a short report explaining the observed bottleneck, not merely screenshots of graphs.


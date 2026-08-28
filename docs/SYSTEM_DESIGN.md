# System design notes

## Why these service boundaries?

Social Service owns strongly consistent facts: identity, passwords, follows, posts, reply relationships, and likes. Feed and Search own disposable read models. If Cassandra or Elasticsearch is lost, replaying retained Kafka events can rebuild those projections; MySQL cannot be reconstructed from them and remains the source of truth.

The gateway demonstrates edge routing and authentication. Docker DNS and environment variables are enough for this local project, so discovery and configuration servers are intentionally excluded.

## Write path and the dual-write problem

Creating content must not commit MySQL and then merely hope a Kafka send succeeds. Save the content row and its JSON outbox row in one database transaction. A scheduled worker later publishes the event and marks the row published only after Kafka acknowledges it.

A crash after publish but before marking can publish twice. Consumers therefore use the content ID as their Cassandra primary-key component and Elasticsearch document ID. This is **at-least-once delivery plus idempotent handling**, not exactly-once end-to-end delivery.

## Replies

Every content row has a `root_id`; replies additionally have a `parent_id`. Fetch only a parent's direct children with a cursor. React recursively renders those pages as the reader expands nodes. This supports arbitrary logical depth without one recursive SQL query or an unbounded JSON response.

## Celebrity problem

Fan-out-on-write gives fast reads for ordinary authors but one celebrity post can create millions of writes. Pulling everything on read avoids that write spike but makes every timeline expensive.

This project uses a hybrid:

1. Always append top-level posts to `author_posts_by_author_day`.
2. If follower count is below `CELEBRITY_THRESHOLD`, also append the ID to each follower's `home_feed_by_user_day`.
3. If the author is a celebrity, skip follower writes.
4. On read, merge the user's precomputed entries with recent author rows for celebrities they follow.
5. Deduplicate IDs, sort newest-first, take 20, and batch-hydrate from Social Service.

The local threshold is 100 so the write-amplification difference is measurable. Production classification would usually use hysteresis, cached follower lists, partitioned fan-out workers, and monitoring rather than switching behavior at one exact count.

## Consistency and CAP thinking

- Registration, following, content creation, and likes use MySQL transactions and prefer consistency.
- Search and feeds are asynchronous and eventually consistent. A successful post may take time to appear.
- Cassandra models the queries in advance and favors continued distributed availability; it is not used for joins or as a relational copy.
- During Social Service failure, already materialized IDs exist in Cassandra, but hydration cannot complete. Lab 6 should return a clear `503` rather than silently serve misleading partial objects.

## Pagination and partitions

MySQL replies use `(created_at, id)` as the stable cursor. Elasticsearch uses `search_after`. Cassandra cursors carry an opaque bucket date and paging state. Day buckets bound partition growth; readers walk backward through days until they fill the requested page. Never expose a cursor's internal format as a public contract.

## Intentional limitations

Following an ordinary author does not backfill old posts. Likes do not alter feed ranking. There is no editing/deletion, refresh token, rate limiting, media, private account, tracing stack, or production service identity. Each is a useful later design exercise after the core data flow is understood.


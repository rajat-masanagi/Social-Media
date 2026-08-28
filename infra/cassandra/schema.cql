CREATE KEYSPACE IF NOT EXISTS text_social
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS text_social.home_feed_by_user_day (
    user_id uuid,
    bucket_day date,
    created_at timestamp,
    post_id uuid,
    author_id uuid,
    PRIMARY KEY ((user_id, bucket_day), created_at, post_id)
) WITH CLUSTERING ORDER BY (created_at DESC, post_id ASC);

CREATE TABLE IF NOT EXISTS text_social.author_posts_by_author_day (
    author_id uuid,
    bucket_day date,
    created_at timestamp,
    post_id uuid,
    PRIMARY KEY ((author_id, bucket_day), created_at, post_id)
) WITH CLUSTERING ORDER BY (created_at DESC, post_id ASC);


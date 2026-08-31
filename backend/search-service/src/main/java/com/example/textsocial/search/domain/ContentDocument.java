package com.example.textsocial.search.domain;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "content-v1", createIndex = false)
public class ContentDocument {
    @Id private UUID id;
    @Field(type = FieldType.Keyword) private UUID rootId;
    @Field(type = FieldType.Keyword) private UUID parentId;
    @Field(type = FieldType.Keyword) private UUID authorId;
    @Field(type = FieldType.Keyword) private String authorUsername;
    @Field(type = FieldType.Text) private String text;
    @Field(type = FieldType.Date, format = DateFormat.date_time) private Instant createdAt;
    @Field(type = FieldType.Keyword) private String contentType;
    public ContentDocument() {}
    public ContentDocument(UUID id, UUID rootId, UUID parentId, UUID authorId, String authorUsername,
                           String text, Instant createdAt, String contentType) {
        this.id=id; this.rootId=rootId; this.parentId=parentId; this.authorId=authorId;
        this.authorUsername=authorUsername; this.text=text; this.createdAt=createdAt; this.contentType=contentType;
    }
    public UUID getId() { return id; }
    public UUID getRootId() { return rootId; }
    public UUID getParentId() { return parentId; }
    public UUID getAuthorId() { return authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
    public String getContentType() { return contentType; }
}

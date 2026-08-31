package com.example.textsocial.social.service;

import com.example.textsocial.social.api.SocialDtos.*;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import com.example.textsocial.social.api.ResourceNotFoundException;
import com.example.textsocial.social.api.SelfFollowException;
import com.example.textsocial.social.api.InvalidCursorException;
import com.example.textsocial.social.domain.Content;
import com.example.textsocial.social.domain.UserAccount;
import com.example.textsocial.social.repository.ContentRepository;
import com.example.textsocial.social.repository.UserRepository;
import com.example.textsocial.social.repository.OutboxRepository;
import com.example.textsocial.social.domain.OutboxEvent;
import com.example.textsocial.events.ContentPublishedV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;

@Service
public class SocialService {
    private final ContentRepository contents; private final UserRepository users; private final JdbcTemplate jdbc; private final OutboxRepository outbox; private final ObjectMapper mapper; private final long threshold;
    public SocialService(ContentRepository contents, UserRepository users, JdbcTemplate jdbc, OutboxRepository outbox, ObjectMapper mapper, @Value("${app.celebrity-threshold:100}") long threshold) { this.contents=contents; this.users=users; this.jdbc=jdbc; this.outbox=outbox; this.mapper=mapper; this.threshold=threshold; }
    public UserResponse user(String username, UUID viewerId) { UserAccount u=users.findByUsername(username.toLowerCase()).orElseThrow(); return new UserResponse(u.getId(),u.getUsername(),u.getFollowerCount(),false); }
    @Transactional public void follow(UUID targetId, UUID viewerId) { if(viewerId.equals(targetId)) throw new SelfFollowException(); requireUser(targetId); int n=jdbc.update("INSERT IGNORE INTO follows(follower_id,followed_id,created_at) VALUES(UUID_TO_BIN(?),UUID_TO_BIN(?),UTC_TIMESTAMP(6))",viewerId.toString(),targetId.toString()); if(n==1) jdbc.update("UPDATE users SET follower_count=follower_count+1 WHERE id=UUID_TO_BIN(?)",targetId.toString()); }
    @Transactional public void unfollow(UUID targetId, UUID viewerId) { int n=jdbc.update("DELETE FROM follows WHERE follower_id=UUID_TO_BIN(?) AND followed_id=UUID_TO_BIN(?)",viewerId.toString(),targetId.toString()); if(n==1) jdbc.update("UPDATE users SET follower_count=GREATEST(0,follower_count-1) WHERE id=UUID_TO_BIN(?)",targetId.toString()); }
    public ContentResponse createPost(CreateContentRequest request, UUID authorId) { return save(null,request,authorId); }
    public ContentResponse reply(UUID parentId, CreateContentRequest request, UUID authorId) { Content p=contents.findById(parentId).orElseThrow(); return save(p,request,authorId); }
    private ContentResponse save(Content parent, CreateContentRequest request, UUID authorId) { UserAccount a=requireUser(authorId); Instant now=Instant.now(); String text=request.text().trim(); UUID id=UUID.randomUUID(); Content c=new Content(id,parent==null?id:parent.getRootId(),parent==null?null:parent.getId(),authorId,text,now); Content saved=contents.save(c); try { var e=new ContentPublishedV1(UUID.randomUUID(),id,saved.getRootId(),saved.getParentId(),authorId,a.getUsername(),text,now,parent==null?ContentPublishedV1.ContentType.POST:ContentPublishedV1.ContentType.REPLY,a.getFollowerCount()>=threshold); outbox.save(new OutboxEvent(UUID.randomUUID(),id,"ContentPublishedV1",mapper.writeValueAsString(e),now)); } catch(Exception ex){ throw new IllegalStateException("Outbox serialization failed",ex); } return new ContentResponse(saved.getId(),saved.getRootId(),saved.getParentId(),authorId,a.getUsername(),text,now,0,0,false); }
    public ContentResponse content(UUID id, UUID viewerId) { Content c=contents.findById(id).orElseThrow(() -> new ResourceNotFoundException("Content")); UserAccount a=requireUser(c.getAuthorId()); long replies=jdbc.queryForObject("SELECT COUNT(*) FROM content WHERE parent_id=UUID_TO_BIN(?)",Long.class,id.toString()); long likes=jdbc.queryForObject("SELECT COUNT(*) FROM likes WHERE content_id=UUID_TO_BIN(?)",Long.class,id.toString()); boolean liked=viewerId!=null&&jdbc.queryForObject("SELECT COUNT(*) FROM likes WHERE user_id=UUID_TO_BIN(?) AND content_id=UUID_TO_BIN(?)",Long.class,viewerId.toString(),id.toString())>0; return new ContentResponse(c.getId(),c.getRootId(),c.getParentId(),c.getAuthorId(),a.getUsername(),c.getText(),c.getCreatedAt(),replies,likes,liked); }
    public PageResponse<ContentResponse> replies(UUID parentId, String cursor, int limit, UUID viewerId) {
        requireContent(parentId);
        if (cursor != null) {
            try {
                String decoded = new String(java.util.Base64.getUrlDecoder().decode(cursor), java.nio.charset.StandardCharsets.UTF_8);
                String[] parts = decoded.split("\\|", 2);
                if (parts.length != 2 || !parts[0].matches("\\d+") || !parts[1].matches("[0-9a-fA-F-]{36}")) throw new IllegalArgumentException();
            }
            catch (IllegalArgumentException ex) { throw new InvalidCursorException(); }
        }
        var items=contents.findAll().stream().filter(c->parentId.equals(c.getParentId()))
                .sorted(java.util.Comparator.comparing(Content::getCreatedAt).reversed().thenComparing(Content::getId))
                .limit(Math.min(20,Math.max(1,limit))).map(c->content(c.getId(),viewerId)).toList();
        return new PageResponse<>(items,null);
    }
    @Transactional public void like(UUID contentId, UUID viewerId) { requireContent(contentId); jdbc.update("INSERT IGNORE INTO likes(user_id,content_id,created_at) VALUES(UUID_TO_BIN(?),UUID_TO_BIN(?),UTC_TIMESTAMP(6))",viewerId.toString(),contentId.toString()); }
    @Transactional public void unlike(UUID contentId, UUID viewerId) { requireContent(contentId); jdbc.update("DELETE FROM likes WHERE user_id=UUID_TO_BIN(?) AND content_id=UUID_TO_BIN(?)",viewerId.toString(),contentId.toString()); }
    public FollowerPage followers(UUID authorId, String cursor, int limit) { requireUser(authorId); var ids=jdbc.query("SELECT BIN_TO_UUID(follower_id) FROM follows WHERE followed_id=UUID_TO_BIN(?) ORDER BY follower_id LIMIT ?",(rs,n)->UUID.fromString(rs.getString(1)),authorId.toString(),Math.min(100,limit)); return new FollowerPage(ids,null); }
    public List<UUID> followedCelebrities(UUID userId) { return jdbc.query("SELECT BIN_TO_UUID(u.id) FROM follows f JOIN users u ON u.id=f.followed_id WHERE f.follower_id=UUID_TO_BIN(?) AND u.follower_count>=?",(rs,n)->UUID.fromString(rs.getString(1)),userId.toString(),threshold); }
    public List<ContentResponse> batch(BatchContentRequest request) { return request.ids()==null?List.of():request.ids().stream().map(id->{try{return content(id,request.viewerId());}catch(Exception e){return null;}}).filter(java.util.Objects::nonNull).toList(); }
    private UserAccount requireUser(UUID id){ return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User")); }
    private void requireContent(UUID id){ if(!contents.existsById(id)) throw new ResourceNotFoundException("Content"); }
}

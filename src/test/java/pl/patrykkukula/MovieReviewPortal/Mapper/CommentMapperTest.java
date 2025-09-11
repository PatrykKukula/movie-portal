package pl.patrykkukula.MovieReviewPortal.Mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {
    private Comment comment;
    private Comment reply;
    private Topic topic;
    private UserEntity user;
    private Map<Long, Long> commentsCount;
    
    @BeforeEach
    public void setUp(){
        comment = Comment.builder()
                .commentId(1L)
                .text("Comment")
                .commentIdInPost(1L)
                .isReply(false)
                .repliedCommentId(0L)
                .build();
        comment.setCreatedAt(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12,0)));
        comment.setUpdatedAt(null);
        
        topic = Topic.builder().topicId(1L).entityId(1L).build();
        comment.setTopic(topic);

        comment.setCreatedAt(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12,0)));
        comment.setUpdatedAt(null);

        user = UserEntity.builder()
                .userId(1L)
                .username("User")
                .registeredAt(LocalDateTime.of(LocalDate.of(1999,1,1), LocalTime.of(12,0)))
                .build();
        comment.setUser(user);

        reply = Comment.builder()
                .commentId(2L)
                .text("Reply")
                .commentIdInPost(2L)
                .isReply(true)
                .repliedCommentId(1L)
                .build();
        reply.setTopic(topic);

        reply.setCreatedAt(LocalDateTime.of(LocalDate.of(2000,2,1), LocalTime.of(12,0)));
        reply.setUpdatedAt(null);
        reply.setUser(user);

        commentsCount = new HashMap<>();
        commentsCount.put(1L, 30L);
    }

    @Test
    public void shouldMapCommentToCommentDtoWithUserCorrectly(){
        CommentDtoWithUser mappedComment = CommentMapper.mapToCommentDtoWithUser(comment, commentsCount);

        assertEquals("Comment", mappedComment.getText());
        assertEquals(1L, mappedComment.getCommentId());
        assertEquals(1L, mappedComment.getCommentIdInPost());
        assertEquals(0L, mappedComment.getRepliedCommentId());
        assertFalse(mappedComment.isReply());
        assertEquals("2000-01-01 12:00", mappedComment.getCreatedAt());
        assertEquals("", mappedComment.getUpdatedAt());
        assertEquals("User", mappedComment.getAuthor());
        assertEquals(1L, mappedComment.getTopicId());
    }
    @Test
    public void shouldMapToCommentDtoWithRepliesCorrectly(){
        CommentDtoWithReplies mappedComment = CommentMapper.mapToCommentDtoWithReplies(comment, List.of(reply));

        assertEquals("Comment", mappedComment.getText());
        assertEquals(1L, mappedComment.getCommentId());
        assertEquals(0L, mappedComment.getRepliedCommentId());
        assertFalse(mappedComment.isReply());
        assertEquals("2000-01-01 12:00", mappedComment.getCreatedAt());
        assertEquals("", mappedComment.getUpdatedAt());
        assertEquals("User", mappedComment.getAuthor());
        assertEquals(1L, mappedComment.getTopicId());

        assertEquals("Reply", mappedComment.getReplies().getFirst().getText());
        assertEquals(2L, mappedComment.getReplies().getFirst().getCommentId());
        assertEquals(1L,mappedComment.getReplies().getFirst().getRepliedCommentId());
        assertTrue(mappedComment.getReplies().getFirst().isReply());
        assertEquals("2000-02-01 12:00", mappedComment.getReplies().getFirst().getCreatedAt());
    }
}

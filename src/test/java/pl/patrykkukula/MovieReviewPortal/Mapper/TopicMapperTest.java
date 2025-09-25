package pl.patrykkukula.MovieReviewPortal.Mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.MainViewTopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TopicMapperTest {
    private Topic topic;
    private TopicDto topicDto;
    private Map<Long, Long> commentsCount;
    private Comment comment;

    @BeforeEach
    public void setUp(){
        topic =  Topic.builder()
                .topicId(1L)
                .title("Topic title")
                .build();
        topic.setCreatedBy("User");
        topic.setCreatedAt(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12, 0)));
        topicDto = TopicDto.builder()
                .title("Topic")
                .entityId(1L)
                .entityType("Actor")
                .build();

        comment = Comment.builder()
                .commentId(1L)
                .text("Comment")
                .commentIdInPost(1L)
                .isReply(false)
                .repliedCommentId(0L)
                .build();
        comment.setCreatedAt(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12,0)));
        comment.setUpdatedAt(null);

        commentsCount = new HashMap<>();
        commentsCount.put(1L, 30L);

        comment = setUpComment();

    }

    @Test
    public void shouldMapTopicDtoToTopicCorrectly(){
        Topic mappedTopic = TopicMapper.mapToTopic(topicDto);

        assertEquals("Topic", mappedTopic.getTitle());
        assertEquals(1L, mappedTopic.getEntityId());
        assertEquals("Actor", mappedTopic.getEntityType());
    }
    @Test
    public void shouldMapTopicToTopicDtoToDisplayCorrectly() {
        try (MockedStatic<CommentMapper> mock = mockStatic(CommentMapper.class)) {
            mock.when(() -> CommentMapper.mapToCommentDtoWithUser(any(Comment.class), anyMap())).thenReturn(new CommentDtoWithUser());

            TopicDtoToDisplay topicDtoToDisplay = TopicMapper.mapToTopicDtoToDisplay(topic, commentsCount, List.of(comment));

            assertEquals("Topic title", topicDtoToDisplay.getTitle());
            assertEquals("User", topicDtoToDisplay.getAuthor());
            assertEquals(1, topicDtoToDisplay.getComments().size());
        }
    }
    @Test
    public void shouldMapTopicToTopicDtoBasicCorrectly(){
        TopicDtoBasic mappedTopic = TopicMapper.mapToTopicDtoBasic(topic);

        assertEquals(1L, mappedTopic.getId());
        assertEquals("Topic title", mappedTopic.getTitle());
        assertEquals("User", mappedTopic.getCreatedBy());
        assertEquals(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12,0)), mappedTopic.getCreatedAt());
    }
    @Test
    public void shouldMapTopicToMainViewTopicDtoCorrectly(){
        topic.setUser(UserEntity.builder().username("User").build());
        MainViewTopicDto mappedTopic = TopicMapper.mapToMainViewTopicDto(topic, 100L, "Actor", "Actor");

        assertEquals(1L, mappedTopic.getId());
        assertEquals("Topic title", mappedTopic.getTitle());
        assertEquals("User", topic.getCreatedBy());
        assertEquals(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12,0)), mappedTopic.getCreatedAt());
        assertEquals(100L, mappedTopic.getCommentCount());
        assertEquals("Actor", mappedTopic.getEntityName());
        assertEquals("Actor", mappedTopic.getEntityType());
    }

    private Comment setUpComment(){
        Comment comment = Comment.builder()
                .commentId(1L)
                .text("Comment")
                .commentIdInPost(1L)
                .isReply(false)
                .repliedCommentId(0L)
                .build();
        comment.setCreatedAt(LocalDateTime.of(LocalDate.of(2000,1,1), LocalTime.of(12,0)));
        comment.setUpdatedAt(null);

        UserEntity user = UserEntity.builder()
                .userId(1L)
                .username("User")
                .registeredAt(LocalDateTime.of(LocalDate.of(1999,1,1), LocalTime.of(12,0)))
                .build();
        comment.setUser(user);
        return comment;
    }
}

package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    @Test
    public void shouldMapCommentToCommentDtoWithUserCorrectly(){
        Topic topic = Topic.builder().topicId(1L).build();
        Comment comment = Comment.builder()
                 .text("Comment")
                 .commentIdInPost(1L)
                 .topic(topic)
                 .build();
        comment.setCreatedBy("User");

        CommentDtoWithUser commentDtoWithUser = CommentMapper.mapToCommentDtoWithUser(comment);

        assertEquals("Comment", commentDtoWithUser.getText());
        assertEquals(1L, commentDtoWithUser.getCommentIdInPost());
        assertEquals(1L, commentDtoWithUser.getTopicId());
        assertEquals("User", commentDtoWithUser.getUser());
    }
}

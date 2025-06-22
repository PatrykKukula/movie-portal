package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class TopicMapperTest {

    @Test
    public void shouldMapTopicDtoToTopicCorrectly(){
        TopicDto topicDto = TopicDto.builder().title("Topic title").build();
        Topic topic = TopicMapper.mapToTopic(topicDto, createMovie());

        assertEquals("Topic title", topic.getTitle());
        assertEquals("Movie title", topic.getMovie().getTitle());
    }
    @Test
    public void shouldMapTopicToTopicDtoToDisplayCorrectly(){
        Movie movie = createMovie();
        Topic topic = createTopic(movie);

        TopicDtoToDisplay topicDtoToDisplay = TopicMapper.mapToTopicDtoToDisplay(topic, List.of(createComment(topic)));

        assertEquals("Topic title", topicDtoToDisplay.getTitle());
        assertEquals("Movie title", topic.getMovie().getTitle());
        assertEquals("User",topicDtoToDisplay.getAuthor());
        assertEquals(1, topicDtoToDisplay.getComments().size());
    }
    @Test
    public void shouldMapTopicToTopicDtoBasicCorrectly(){
        Topic topic = createTopic(createMovie());

        TopicDtoBasic topicDtoBasic = TopicMapper.mapToTopicDtoBasic(topic);

        assertEquals("Topic title", topicDtoBasic.getTitle());
        assertEquals("Movie title", topic.getMovie().getTitle());
        assertEquals("User",topicDtoBasic.getAuthor());
    }

    private Comment createComment(Topic topic){
        Comment comment = Comment.builder()
                .text("Comment")
                .commentIdInPost(1L)
                .topic(topic)
                .build();
        comment.setCreatedBy("User");
        return comment;
    }
    private Topic createTopic(Movie movie){
        Topic topic =  Topic.builder()
                .topicId(1L)
                .title("Topic title")
                .movie(movie)
                .build();
        topic.setCreatedBy("User");
        return topic;
    }
    private Movie createMovie(){
        return Movie.builder()
                .title("Movie title")
                .build();
    }
}

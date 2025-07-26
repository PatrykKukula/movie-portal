package pl.patrykkukula.MovieReviewPortal.Mapper;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import java.util.List;

public class TopicMapper {

    public static Topic mapToTopic(TopicDto topicDto) {
        return Topic.builder()
                .title(topicDto.getTitle())
                .entityId(topicDto.getEntityId())
                .entityType(topicDto.getEntityType())
                .build();
    }
    public static TopicDtoToDisplay mapToTopicDtoToDisplay(Topic topic, List<Comment> comments){
        List<CommentDtoWithUser> commentsDto = comments.stream()
                .map(CommentMapper::mapToCommentDtoWithUser)
                .toList();

        return TopicDtoToDisplay.builder()
                .topicId(topic.getTopicId())
                .title(topic.getTitle())
                .comments(commentsDto)
                .author(topic.getCreatedBy())
                .build();
    }
    public static TopicDtoBasic mapToTopicDtoBasic(Topic topic){
        return TopicDtoBasic.builder()
                .id(topic.getTopicId())
                .title(topic.getTitle())
                .createdBy(topic.getCreatedBy())
                .createdAt(topic.getCreatedAt())
                .build();
    }
}

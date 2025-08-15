package pl.patrykkukula.MovieReviewPortal.Mapper;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.MainViewTopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TopicMapper {

    public static Topic mapToTopic(TopicDto topicDto) {
        return Topic.builder()
                .title(topicDto.getTitle())
                .entityId(topicDto.getEntityId())
                .entityType(topicDto.getEntityType())
                .build();
    }
    public static TopicDtoToDisplay mapToTopicDtoToDisplay(Topic topic, List<Comment> comments, Map<Long, Long> commentsCount, List<Comment> allCommentsWithUsers){
        List<CommentDtoWithUser> commentsDto = allCommentsWithUsers.stream()
                .map(comment -> CommentMapper.mapToCommentDtoWithUser(comment, commentsCount))
                .sorted(Comparator.comparing(comment -> comment.getCreatedAt()))
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
    public static MainViewTopicDto mapToMainViewTopicDto(Topic topic, long commentCount, String entityName, String entityType){
        return MainViewTopicDto.builder()
                .id(topic.getTopicId())
                .title(topic.getTitle())
                .createdBy(topic.getUser().getUsername())
                .createdAt(topic.getCreatedAt())
                .entityId(topic.getEntityId())
                .commentCount(commentCount)
                .entityName(entityName)
                .entityType(entityType)
                .userId(topic.getUser().getUserId())
                .build();
    }
}

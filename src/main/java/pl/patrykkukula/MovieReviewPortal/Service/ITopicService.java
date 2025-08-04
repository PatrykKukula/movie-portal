package pl.patrykkukula.MovieReviewPortal.Service;

import org.springframework.data.domain.Page;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoWithCommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicUpdateDto;

import java.util.List;

public interface ITopicService {

    Long createTopic(TopicDtoWithCommentDto topicWithComment, Long movieId, String entityType);
    void deleteTopic(Long topicId);
    TopicDtoToDisplay findTopicById(Long topicId);
    Page<TopicDtoBasic> findAllTopics(int page, int size, String sorted, String entityType, Long entityId);
    List<TopicDtoBasic> findTopicsByTitle(String title, String sorted);
    void updateTopic(Long topicId, TopicUpdateDto topicUpdateDto);
}

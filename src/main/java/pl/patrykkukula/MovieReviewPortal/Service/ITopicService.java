package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoWithCommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicUpdateDto;

import java.util.List;

public interface ITopicService {

    Long createTopic(TopicDtoWithCommentDto topicWithComment, Long movieId);
    void deleteTopic(Long topicId);
    TopicDtoToDisplay findTopicById(Long topicId);
    List<TopicDtoBasic> findAllTopics(String sorted);
    List<TopicDtoBasic> findTopicsByTitle(String title, String sorted);
    void updateTopic(Long topicId, TopicUpdateDto topicUpdateDto);
}

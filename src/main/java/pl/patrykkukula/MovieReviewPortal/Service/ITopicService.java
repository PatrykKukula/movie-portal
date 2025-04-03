package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.*;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.TopicUpdateDto;

import java.util.List;

public interface ITopicService {

    Long createTopic(TopicDtoWithCommentDto topicWithComment, Long movieId);
    void deleteTopic(Long topicId);
    TopicDtoToDisplay findTopicById(Long topicId);
    List<TopicDtoBasic> findAllTopics(String sorted);
    List<TopicDtoBasic> findTopicsByTitle(String title, String sorted);
    void updateTopic(Long topicId, TopicUpdateDto topicUpdateDto);
}

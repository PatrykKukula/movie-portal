package pl.patrykkukula.MovieReviewPortal.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.*;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.TopicUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.TopicMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Service.ITopicService;
import java.util.List;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.*;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements ITopicService {
    private final TopicRepository topicRepository;
    private final UserEntityRepository userEntityRepository;
    private final MovieRepository movieRepository;

    @Transactional
    @Override
    public Long createTopic(TopicDtoWithCommentDto topicWithComment, Long movieId) {
        validateId(movieId);
        UserEntity user = getUserEntity();
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie","Movie id", String.valueOf(movieId)));
        Topic topic = TopicMapper.mapToTopic(topicWithComment.getTopic(), movie);
        topic.setUser(user);
        Comment comment = Comment.builder()
                .commentIdInPost(1L)
                .text(topicWithComment.getComment().getText())
                .topic(topic)
                .user(user)
                .build();
        topic.setComments(List.of(comment));
        Topic savedTopic = topicRepository.save(topic);
        return savedTopic.getTopicId();
    }
    @Override
    public void deleteTopic(Long topicId) {
        validateId(topicId);
        UserEntity userEntity = getUserEntity();
        Topic topic = topicRepository.findByIdWithUser(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic", "Topic id", String.valueOf(topicId)));
        if (!userEntity.getUserId().equals(topic.getUser().getUserId())) throw new IllegalResourceModifyException("You are not author of this topic");
        topicRepository.deleteById(topicId);
    }
    @Override
    public TopicDtoToDisplay findTopicById(Long topicId) {
        validateId(topicId);
        Topic topic = topicRepository.findByTopicIdWithComments(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "Topic id", String.valueOf(topicId)));
        List<Comment> comments = topic.getComments();
        TopicDtoToDisplay topicDtoToDisplay = TopicMapper.mapToTopicDtoToDisplay(topic, comments);
        long postCount = comments.size();
        topicDtoToDisplay.setPostCount(postCount);
        return topicDtoToDisplay;
    }
    @Override
    public List<TopicDtoBasic> findAllTopics(String sorted) {
        String validatedSorting = validateSorting(sorted);
        List<Topic> topics = validatedSorting.equals("ASC") ?
                topicRepository.findAllOrderByTopicIdAsc():
                topicRepository.findAllOrderByTopicIdDesc();
        return topics.stream().map(topic -> {
            TopicDtoBasic topicDtoBasic = TopicMapper.mapToTopicDtoBasic(topic);
            topicDtoBasic.setPostCount(topic.getComments().size());
            return topicDtoBasic;
        }).toList();
    }
    @Override
    public List<TopicDtoBasic> findTopicsByTitle(String title, String sorted) {
        String validatedSorting = validateSorting(sorted);
        List<Topic> topics = validatedSorting.equals("ASC") ?
                topicRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(title).stream().toList() :
                topicRepository.findByTitleContainingIgnoreCaseOrderByTitleDesc(title).stream().toList();
        return topics.stream().map(topic -> {
            TopicDtoBasic topicDtoBasic = TopicMapper.mapToTopicDtoBasic(topic);
            topicDtoBasic.setPostCount(topic.getComments().size());
            return topicDtoBasic;
        }).toList();
    }
    @Override
    @Transactional
    public void updateTopic(Long topicId, TopicUpdateDto topicUpdateDto) {
        validateId(topicId);
        UserEntity userEntity = getUserEntity();
        Topic topic = topicRepository.findByIdWithUser(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic", "Topic id", String.valueOf(topicId)));
        if (!userEntity.getUserId().equals(topic.getUser().getUserId())) throw new IllegalResourceModifyException("You are not author of this topic");
        topic.setTitle(topicUpdateDto.getTitle());
        topicRepository.save(topic);
    }
    private UserEntity getUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("User is not logged in");
        }
        User user = (User) auth.getPrincipal();
        return userEntityRepository.findByUsername(user.getUsername()).orElseThrow(() -> new ResourceNotFoundException("Account", "email", user.getUsername()));
    }
}

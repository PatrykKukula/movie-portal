package pl.patrykkukula.MovieReviewPortal.Service.Impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoWithCommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicUpdateDto;
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
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.ITopicService;
import java.util.List;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements ITopicService {
    private final TopicRepository topicRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Long createTopic(TopicDtoWithCommentDto topicWithComment, Long entityId, String entityType) {
        validateId(entityId);
        UserEntity user = userDetailsService.getUserEntity();

        Topic topic = TopicMapper.mapToTopic(topicWithComment.getTopic());
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void deleteTopic(Long topicId) {
        validateId(topicId);
        UserEntity userEntity = userDetailsService.getUserEntity();
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
    public Page<TopicDtoBasic> findAllTopics(int page, int size, String sorted, String entityType, Long entityId) {
        String validatedSorting = validateSorting(sorted);
        Sort sort = validatedSorting.equals("ASC") ?
                Sort.by("topicId").ascending() :
                Sort.by("topicId").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Topic> topicsPage = topicRepository.findAllByEntityTypeAndEntityId(entityType, entityId, pageable);
        log.info("topic page size{}: ", topicsPage.getTotalElements());

        return topicsPage.map(topic -> {
            TopicDtoBasic topicDtoBasic = TopicMapper.mapToTopicDtoBasic(topic);
            topicDtoBasic.setPostCount(topic.getComments().size());
            return topicDtoBasic;
        });
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
        UserEntity userEntity = userDetailsService.getUserEntity();
        Topic topic = topicRepository.findByIdWithUser(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic", "Topic id", String.valueOf(topicId)));
        if (!userEntity.getUserId().equals(topic.getUser().getUserId())) throw new IllegalResourceModifyException("You are not author of this topic");
        topic.setTitle(topicUpdateDto.getTitle());
        topicRepository.save(topic);
    }
}

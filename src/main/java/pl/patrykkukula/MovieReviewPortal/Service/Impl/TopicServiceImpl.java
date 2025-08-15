package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.*;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.TopicMapper;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.*;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.ITopicService;

import java.util.*;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements ITopicService {
    private final TopicRepository topicRepository;
    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
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

        List<Comment> allCommentsWithUsers = commentRepository.findAllCommentsForTopicWithUsers(topicId);

        List<Tuple> tuples = commentRepository.countCommentsForUserByTopicId(topicId);
        Map<Long, Long> commentsCount = new HashMap<>();
        for (Tuple tuple : tuples) {
            Long userId = tuple.get("userId", Long.class);
            long count = tuple.get("count", Long.class);
            commentsCount.put(userId, count);
        }

        TopicDtoToDisplay topicDtoToDisplay = TopicMapper.mapToTopicDtoToDisplay(topic, comments, commentsCount, allCommentsWithUsers);

        topicDtoToDisplay.setPostCount((long)comments.size());

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

    @Override
    public List<MainViewTopicDto> fetchLatestTopics() {
        return topicRepository.findLatestTopics().stream()
                .map(topic -> {
                    String type = topic.getEntityType();
                    log.info("created time:{} ", topic.getCreatedAt());
                    switch (type){
                        case "actor" -> {
                            Optional<Actor> actor = actorRepository.findById(topic.getEntityId());
                            String entityName = actor.map(value -> value.getFirstName() + " " + value.getLastName()).orElse(" ");
                            return TopicMapper.mapToMainViewTopicDto(topic, topic.getComments().size(), entityName, type);
                        }
                        case "director" -> {
                            Optional<Director> director = directorRepository.findById(topic.getEntityId());
                            String entityName = director.map(value -> value.getFirstName() + " " + value.getLastName()).orElse(" ");
                            return TopicMapper.mapToMainViewTopicDto(topic, topic.getComments().size(), entityName, type);
                        }
                        case "movie" -> {
                            Optional<Movie> movie = movieRepository.findById(topic.getEntityId());
                            String entityName = movie.map(Movie::getTitle).orElse("");
                            return TopicMapper.mapToMainViewTopicDto(topic, topic.getComments().size(), entityName, type);
                        }
                        default -> {
                            return new MainViewTopicDto();
                        }
                    }
                }).toList();
    }
}

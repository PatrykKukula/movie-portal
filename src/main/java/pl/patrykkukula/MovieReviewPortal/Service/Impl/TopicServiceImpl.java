package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = "latest-topics", allEntries = true)
    public Long createTopic(TopicDtoWithCommentDto topicWithComment, Long entityId, String entityType) {
        validateId(entityId);
        UserEntity user = getLoggedUserEntity();
            boolean isPresent = false;
            switch (entityType) {
                case "actor" -> isPresent = actorRepository.findById(entityId).isPresent();
                case "director" -> isPresent = directorRepository.findById(entityId).isPresent();
                case "movie" -> isPresent = movieRepository.findById(entityId).isPresent();
            }
            if (isPresent) {
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
            else throw new ResourceNotFoundException(entityType, "Id", entityId.toString());
    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = "topic")
    public void deleteTopic(Long topicId) {
        validateId(topicId);
        UserEntity userEntity = getLoggedUserEntity();
        Topic topic = topicRepository.findByIdWithUser(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic", "Topic id", String.valueOf(topicId)));
        if (!userEntity.getUserId().equals(topic.getUser().getUserId())) throw new IllegalResourceModifyException("You are not author of this topic");
        topicRepository.deleteById(topicId);
    }
    @Override
    @Cacheable(value = "topic")
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

        TopicDtoToDisplay topicDtoToDisplay = TopicMapper.mapToTopicDtoToDisplay(topic, commentsCount, allCommentsWithUsers);

        topicDtoToDisplay.setPostCount((long)comments.size());

        return topicDtoToDisplay;
    }
    @Override
    public Page<TopicDtoBasic> findAllTopics(int page, int size, String sorted, String entityType, Long entityId) {
        log.info("Invoking findAllTopics with parameters page:{} size:{}, sorted:{}, entityType:{}, entityId:{}",
                page,size,sorted,entityType,entityId);
        String validatedSorting = validateSorting(sorted);
        Sort sort = validatedSorting.equals("ASC") ?
                Sort.by("createdAt").ascending() :
                Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Topic> topicsPage = topicRepository.findAllByEntityTypeAndEntityId(entityType, entityId, pageable);
        log.info("Found topics pages:{} and content:{}  ", topicsPage.getTotalPages(), topicsPage.getContent().size());

        return topicsPage.map(topic -> {
            TopicDtoBasic topicDtoBasic = TopicMapper.mapToTopicDtoBasic(topic);
            topicDtoBasic.setPostCount(topic.getComments().size());
            return topicDtoBasic;
        });
    }
    @Override
    @Cacheable(value = "topics-by-title", unless = "#result.isEmpty() or #title.length() <= 3")
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
    @PreAuthorize("hasAnyRole('ADMIN','USER', 'MODERATOR')")
    @CacheEvict(value = "topic", key = "#topicId")
    public void updateTopic(Long topicId, TopicUpdateDto topicUpdateDto) {
        validateId(topicId);
        UserEntity userEntity = getLoggedUserEntity();
        Topic topic = topicRepository.findByIdWithUser(topicId).orElseThrow(() -> new ResourceNotFoundException("Topic", "Topic id", String.valueOf(topicId)));
        if (!userEntity.getUserId().equals(topic.getUser().getUserId())) throw new IllegalResourceModifyException("You are not author of this topic");
        topic.setTitle(topicUpdateDto.getTitle());
        topicRepository.save(topic);
    }
    @Override
    @Cacheable(value = "latest-topics")
    public List<MainViewTopicDto> fetchLatestTopics() {
        return topicRepository.findLatestTopics().stream()
                .map(topic -> {
                    String type = topic.getEntityType();
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
                })
                .limit(5)
                .toList();
    }
    private UserEntity getLoggedUserEntity(){
        return userDetailsService.getLoggedUserEntity().orElseThrow(() -> new AccessDeniedException("User not logged in"));
    }
}

package pl.patrykkukula.MovieReviewPortal.Service;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.*;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.*;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private TopicServiceImpl topicService;

    private Movie movie;
    private Topic topic1;
    private Topic topic2;
    private Actor actor;
    private Director director;
    private UserEntity user;
    private TopicDtoWithCommentDto topicDtoWithCommentDto;
    private List<Comment> comments = new ArrayList<>();
    private Comment comment;
    private LocalDate date = LocalDate.of(2020,1,1);

    @BeforeEach
    void setUp() {
        actor = Actor.builder()
                .actorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        director = Director.builder()
                .directorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        movie = Movie.builder()
                .movieId(1L)
                .title("Movie")
                .description("Description")
                .releaseDate(date)
                .category(MovieCategory.ACTION)
                .actors(List.of(actor))
                .director(director)
                .build();
        topic1 = Topic.builder()
                .topicId(1L)
                .title("Topic")
                .comments(Collections.emptyList())
                .user(setCurrentUser())
                .entityId(1L)
                .entityType("movie")
                .build();
        topic2 = Topic.builder()
                .topicId(2L)
                .title("New Topic")
                .entityId(1L)
                .entityType("actor")
                .comments(Collections.emptyList())
                .user(setCurrentUser())
                .build();
        user = setCurrentUser();
        topicDtoWithCommentDto = setTopicDtoWithCommentDto();
        comment = Comment.builder()
                .commentId(1L)
                .text("Comment")
                .user(user)
                .topic(topic1)
                .build();
        comments.add(comment);
    }

    @Test
    public void shouldCreateTopicCorrectlyForMovie(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(topicRepository.save(any(Topic.class))).thenReturn(topic1);
        ArgumentCaptor<Topic> captor = ArgumentCaptor.forClass(Topic.class);

        topicService.createTopic(topicDtoWithCommentDto, 1L, "movie");
        verify(topicRepository).save(captor.capture());
        Topic savedTopic = captor.getValue();

        assertEquals("Topic", savedTopic.getTitle());
        assertEquals("movie", savedTopic.getEntityType());
        assertEquals(1, savedTopic.getComments().size());
        verify(movieRepository).findById(1L);
        verifyNoInteractions(directorRepository);
        verifyNoInteractions(actorRepository);
    }
    @Test
    public void shouldCreateTopicCorrectlyForActor(){
        topicDtoWithCommentDto.getTopic().setEntityType("actor");
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor));
        when(topicRepository.save(any(Topic.class))).thenReturn(topic1);
        ArgumentCaptor<Topic> captor = ArgumentCaptor.forClass(Topic.class);

        topicService.createTopic(topicDtoWithCommentDto, 1L, "actor");
        verify(topicRepository).save(captor.capture());
        Topic savedTopic = captor.getValue();

        assertEquals("Topic", savedTopic.getTitle());
        assertEquals("actor", savedTopic.getEntityType());
        assertEquals(1, savedTopic.getComments().size());
        verify(actorRepository).findById(1L);
        verifyNoInteractions(directorRepository);
        verifyNoInteractions(movieRepository);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenCreateTopicAndEntityIdIsInvalid(){
        assertThrows(InvalidIdException.class, () -> topicService.createTopic(topicDtoWithCommentDto, -1L, "actor"));
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenCreateTopicAndNotLoggedId(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> topicService.createTopic(topicDtoWithCommentDto, 1L, "movie"));
        assertEquals("User not logged in", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenCreateTopicAndNoEntityFound(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> topicService.createTopic(topicDtoWithCommentDto, 1L, "movie"));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldDeleteTopicCorrectly(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));

        topicService.deleteTopic(1L);

        verify(topicRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenDeleteTopicAndNotLoggedIn(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> topicService.createTopic(topicDtoWithCommentDto, 1L, "movie"));
        assertEquals("User not logged in", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenRemoveTopicAndNoEntityFound(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> topicService.deleteTopic(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowIllegalResourceModifyExceptionWhenDeletingOtherUserTopic(){
        UserEntity newUser = UserEntity.builder()
                        .userId(2L)
                        .isEnabled(true)
                        .build();
        topic1.setUser(newUser);
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));

        IllegalResourceModifyException ex = assertThrows(IllegalResourceModifyException.class, () -> topicService.deleteTopic(1L));
        assertEquals("You are not author of this topic", ex.getMessage());
    }
    @Test
    public void shouldFindTopicByIdCorrectly(){
        when(topicRepository.findByTopicIdWithComments(anyLong())).thenReturn(Optional.of(topic1));
        when(commentRepository.findAllCommentsForTopicWithUsers(anyLong())).thenReturn(comments);

        Tuple tuple = mock(Tuple.class);
        when(tuple.get("userId", Long.class)).thenReturn(1L);
        when(tuple.get("count", Long.class)).thenReturn(5L);

        when(commentRepository.countCommentsForUserByTopicId(anyLong())).thenReturn(List.of(tuple));

        TopicDtoToDisplay topicById = topicService.findTopicById(1L);

        assertEquals(1,topicById.getComments().size());
        assertEquals("Topic", topicById.getTitle());
        assertEquals(1, topicById.getComments().getFirst().getUserId());
        assertEquals(5, topicById.getComments().getFirst().getUserCommentCount());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenFindTopicByIdAneTopicNotFound(){
        when(topicRepository.findByTopicIdWithComments(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> topicService.findTopicById(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldFindAllTopicsCorrectlyAsc(){
        Page<Topic> page = new PageImpl<>(List.of(topic1, topic2));
        when(topicRepository.findAllByEntityTypeAndEntityId(anyString(), anyLong(), any())).thenReturn(page);

        Page<TopicDtoBasic> topics = topicService.findAllTopics(0, 5, "ASC", "movie", 1L);

        assertEquals(2, topics.getTotalElements());
        assertEquals(1, topics.getTotalPages());
        assertEquals("Topic", topics.get().toList().getFirst().getTitle());
        assertEquals("New Topic", topics.get().toList().get(1).getTitle());
    }
    @Test
    public void shouldFindAllTopicsCorrectlyDesc(){
        Page<Topic> page = new PageImpl<>(List.of(topic2, topic1));
        when(topicRepository.findAllByEntityTypeAndEntityId(anyString(), anyLong(), any())).thenReturn(page);

        Page<TopicDtoBasic> topics = topicService.findAllTopics(0, 5, "DESC", "movie", 1L);

        assertEquals(2, topics.getTotalElements());
        assertEquals(1, topics.getTotalPages());
        assertEquals("New Topic", topics.get().toList().getFirst().getTitle());
        assertEquals("Topic", topics.get().toList().get(1).getTitle());
    }
    @Test
    public void shouldFindAllTopicsCorrectlyDescAndHaveTwoPages(){
        Page<Topic> page = new PageImpl<>(List.of(topic2, topic1), PageRequest.of(0, 1, Sort.by("userId")),2);
        when(topicRepository.findAllByEntityTypeAndEntityId(anyString(), anyLong(), any())).thenReturn(page);

        Page<TopicDtoBasic> topics = topicService.findAllTopics(0, 1, "DESC", "movie", 1L);

        assertEquals(2, topics.getTotalElements());
        assertEquals(2, topics.getTotalPages());
    }
    @Test
    public void shouldReturnEmptyPageWhenFindAllTopicsAndNoTopicsFound(){
        when(topicRepository.findAllByEntityTypeAndEntityId(anyString(), anyLong(), any())).thenReturn(Page.empty());

        Page<TopicDtoBasic> topics = topicService.findAllTopics(0, 1, "ASC", "movie", 1L);

        assertEquals(0, topics.getTotalElements());
    }
    @Test
    public void shouldReturnEmptyListWhenNoTopicsFoundByTitle(){
        when(topicRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(anyString())).thenReturn(Collections.emptyList());

        List<TopicDtoBasic> topics = topicService.findTopicsByTitle("X", "ASC");

        assertEquals(0, topics.size());
    }
    @Test
    public void shouldFindTopicsByTitleCorrectlyAsc(){
        when(topicRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(anyString())).thenReturn(List.of(topic1, topic2));

        List<TopicDtoBasic> topics = topicService.findTopicsByTitle("", "ASC");

        assertEquals(2, topics.size());
        assertEquals("Topic", topics.getFirst().getTitle());
        assertEquals("New Topic", topics.get(1).getTitle());
        verify(topicRepository, times(1)).findByTitleContainingIgnoreCaseOrderByTitleAsc("");
    }
    @Test
    public void shouldFindTopicsByTitleCorrectlyDesc(){
        when(topicRepository.findByTitleContainingIgnoreCaseOrderByTitleDesc(anyString())).thenReturn(List.of(topic2, topic1));

        List<TopicDtoBasic> topics = topicService.findTopicsByTitle("", "DESC");

        assertEquals(2, topics.size());
        assertEquals("New Topic", topics.getFirst().getTitle());
        assertEquals("Topic", topics.get(1).getTitle());
        verify(topicRepository, times(1)).findByTitleContainingIgnoreCaseOrderByTitleDesc("");
    }
    @Test
    public void shouldFindTopicsByTitleCorrectlyAscWithInvalidInputSort(){
        when(topicRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(anyString())).thenReturn(List.of(topic1, topic2));

        List<TopicDtoBasic> topics = topicService.findTopicsByTitle("", "INVALID");

        assertEquals(2, topics.size());
        assertEquals("Topic", topics.getFirst().getTitle());
        assertEquals("New Topic", topics.get(1).getTitle());
        verify(topicRepository, times(1)).findByTitleContainingIgnoreCaseOrderByTitleAsc("");
    }
    @Test
    public void shouldUpdateTopicCorrectly(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));

        ArgumentCaptor<Topic> captor =  ArgumentCaptor.forClass(Topic.class);
        topicService.updateTopic(1L, new TopicUpdateDto("Updated title"));
        verify(topicRepository).save(captor.capture());

       assertEquals("Updated title", captor.getValue().getTitle());
    }
    @Test
    public void shouldThrowIllegalResourceModifyExceptionWhenUpdateOtherUserTopic(){
        UserEntity userEntity = UserEntity.builder().userId(999L).build();
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));

        IllegalResourceModifyException ex = assertThrows(IllegalResourceModifyException.class, () -> topicService.updateTopic(1L, new TopicUpdateDto("Updated title")));
        assertEquals("You are not author of this topic", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenUpdateTopic(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> topicService.updateTopic(1L, new TopicUpdateDto("Updated title")));
        assertEquals("User not logged in", ex.getMessage());
    }
    @Test
    public void shouldFetchLatestTopicsCorrectly(){
        when(topicRepository.findLatestTopics()).thenReturn(List.of(topic1, topic2));

        List<MainViewTopicDto> topics = topicService.fetchLatestTopics();

        assertEquals(2, topics.size());
        assertEquals("movie", topics.getFirst().getEntityType());
        assertEquals("actor", topics.get(1).getEntityType());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchLatestTopicsAndNoTopicsFound(){
        when(topicRepository.findLatestTopics()).thenReturn(Collections.emptyList());

        List<MainViewTopicDto> topics = topicService.fetchLatestTopics();

        assertEquals(0, topics.size());
    }

    private UserEntity setCurrentUser() {
        Role role = Role.builder()
                .roleId(1L)
                .roleName("USER")
                .build();

        return UserEntity.builder()
                .userId(1L)
                .email("user@user.com")
                .password("password")
                .roles(List.of(role))
                .isEnabled(true)
                .build();
    }
    private TopicDtoWithCommentDto setTopicDtoWithCommentDto(){
        TopicDto topicDto = TopicDto.builder()
                .title("Topic")
                .entityId(1L)
                .entityType("movie")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .topicId(1L)
                .isReply(false)
                .replyCommentId(null)
                .build();
        return new TopicDtoWithCommentDto(topicDto, commentDto);
    }
}

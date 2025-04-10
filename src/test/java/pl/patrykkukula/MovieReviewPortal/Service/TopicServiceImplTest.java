package pl.patrykkukula.MovieReviewPortal.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.*;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.TopicUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopicServiceImplTest {
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserEntityRepository userEntityRepository;
    @InjectMocks
    private TopicServiceImpl topicService;

    private Movie movie;
    private Topic topic1;
    private Topic topic2;
    private Actor actor;
    private Director director;
    private LocalDate date = LocalDate.of(2020,01,01);

    @BeforeEach
    void setUp() {
        setAuthentication();
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
                .movie(movie)
                .user(setCurrentUser())
                .build();
        topic2 = Topic.builder()
                .topicId(2L)
                .title("New Topic")
                .comments(Collections.emptyList())
                .movie(movie)
                .user(setCurrentUser())
                .build();
    }
    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldCreateTopicCorrectly(){
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        when(topicRepository.save(any(Topic.class))).thenReturn(topic1);

        Long createdTopic = topicService.createTopic(setTopicDtoWithCommentDto(), 1L);

        assertEquals(1L, createdTopic);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenMovieIdIsInvalid(){

        assertThrows(InvalidIdException.class, () -> topicService.createTopic(setTopicDtoWithCommentDto(), -1L));
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenMovieNotFound(){
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));

        assertThrows(ResourceNotFoundException.class, () -> topicService.createTopic(setTopicDtoWithCommentDto(), 999L));
    }
    @Test
    public void shouldDeleteTopicCorrectly(){
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        doNothing().when(topicRepository).deleteById(anyLong());

        topicService.deleteTopic(1L);

        verify(topicRepository, times(1)).findByIdWithUser(anyLong());
        verify(topicRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowIllegalResourceModifyExceptionWhenDeletingOtherUserTopic(){
        UserEntity newUser = UserEntity.builder()
                        .userId(2L)
                        .isEnabled(true)
                        .build();
        topic1.setUser(newUser);
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));

        assertThrows(IllegalResourceModifyException.class, () -> topicService.deleteTopic(1L));
    }
    @Test
    public void shouldFindTopicByIdCorrectly(){
        when(topicRepository.findByTopicIdWithComments(anyLong())).thenReturn(Optional.of(topic1));

        TopicDtoToDisplay topicById = topicService.findTopicById(1L);

        assertEquals(0,topicById.getComments().size());
        assertEquals("Topic", topicById.getTitle());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenTopicNotFound(){
        when(topicRepository.findByTopicIdWithComments(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> topicService.findTopicById(1L));
    }
    @Test
    public void shouldFindAllTopicsCorrectlyAsc(){
        when(topicRepository.findAllOrderByTopicIdAsc()).thenReturn(List.of(topic1, topic2));

        List<TopicDtoBasic> topics = topicService.findAllTopics("ASC");

        assertEquals(2, topics.size());
        assertEquals("Topic", topics.get(0).getTitle());
        assertEquals("New Topic", topics.get(1).getTitle());
    }
    @Test
    public void shouldFindAllTopicsCorrectlyDesc(){
        when(topicRepository.findAllOrderByTopicIdDesc()).thenReturn(List.of(topic2, topic1));

        List<TopicDtoBasic> topics = topicService.findAllTopics("DESC");

        assertEquals(2, topics.size());
        assertEquals("Topic", topics.get(1).getTitle());
        assertEquals("New Topic", topics.get(0).getTitle());
    }
    @Test
    public void shouldFindAllTopicsCorrectlyAscWithInvalidInputSorting(){
        when(topicRepository.findAllOrderByTopicIdAsc()).thenReturn(List.of(topic1, topic2));

        List<TopicDtoBasic> topics = topicService.findAllTopics("X");

        assertEquals(2, topics.size());
        assertEquals("Topic", topics.get(0).getTitle());
        assertEquals("New Topic", topics.get(1).getTitle());
    }
    @Test
    public void shouldReturnEmptyListWhenNoTopicsFoundByTitle(){
        when(topicRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(anyString())).thenReturn(Collections.emptyList());

        List<TopicDtoBasic> topics = topicService.findTopicsByTitle("X", "ASC");

        assertEquals(0, topics.size());
    }
    @Test
    public void shouldUpdateTopicCorrectly(){
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(topicRepository.findByIdWithUser(anyLong())).thenReturn(Optional.of(topic1));

        ArgumentCaptor<Topic> captor =  ArgumentCaptor.forClass(Topic.class);
        topicService.updateTopic(1L, new TopicUpdateDto("Updated title"));
        verify(topicRepository).save(captor.capture());

       assertEquals("Updated title", captor.getValue().getTitle());
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
    private void setAuthentication(){
        User user = new User("user@user.com","password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    private TopicDtoWithCommentDto setTopicDtoWithCommentDto(){
        TopicDto topicDto = TopicDto.builder()
                .title("Topic")
                .movieId(1L)
                .build();
        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .topicId(1L)
                .commentIdInPost(1L)
                .build();
        return new TopicDtoWithCommentDto(topicDto, commentDto);
    }
}

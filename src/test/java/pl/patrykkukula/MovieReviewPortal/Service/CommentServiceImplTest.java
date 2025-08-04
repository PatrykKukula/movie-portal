//package pl.patrykkukula.MovieReviewPortal.Service;
//
//import jakarta.persistence.Tuple;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
//import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
//import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
//import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
//import pl.patrykkukula.MovieReviewPortal.Model.*;
//import pl.patrykkukula.MovieReviewPortal.Repository.CommentRepository;
//import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
//import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
//import pl.patrykkukula.MovieReviewPortal.Service.Impl.CommentServiceImpl;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CommentServiceImplTest {
//    @Mock
//    private CommentRepository commentRepository;
//    @Mock
//    private UserEntityRepository userEntityRepository;
//    @Mock
//    private TopicRepository topicRepository;
//    @Mock
//    private Tuple tuple;
//    @InjectMocks
//    private CommentServiceImpl commentService;
//
//    private Topic topic;
//    private CommentDto commentDto;
//    private Comment comment1;
//    private Comment comment2;
//
//    @BeforeEach
//    void setUp(){
//        setAuthentication();
//        topic = Topic.builder()
//                .topicId(1L)
//                .title("Topic")
//                .comments(Collections.emptyList())
//                .movie(new Movie())
//                .user(setCurrentUser())
//                .build();
//        commentDto = CommentDto.builder()
//                .text("Comment")
//                .topicId(1L)
//                .commentIdInPost(1L)
//                .build();
//        comment1 = Comment.builder()
//                .commentId(1L)
//                .text("Comment")
//                .topic(topic)
//                .user(setCurrentUser())
//                .build();
//        comment2 = Comment.builder()
//                .commentId(2L)
//                .text("New Comment")
//                .topic(topic)
//                .user(setCurrentUser())
//                .build();
//
//    }
//    @AfterEach
//    void clear(){
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    public void shouldAddCommentCorrectly() throws Exception{
//        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.of(tuple));
//        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);
//        when(tuple.get("topic",Topic.class)).thenReturn(topic);
//        when(tuple.get("maxId",Long.class)).thenReturn(1L);
//
//        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
//
//        Long commentId = commentService.addComment(commentDto);
//        verify(commentRepository).save(captor.capture());
//
//        assertEquals(1L, commentId);
//        assertEquals("Comment", captor.getValue().getText());
//        assertEquals(2L, captor.getValue().getCommentIdInPost());
//    }
//    @Test
//    public void shouldThrowResourceNotFoundExceptionWhenTopicNotFound() throws Exception{
//        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.empty());
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentDto));
//        assertEquals("Resource Topic not found for field: topic id and value: 1", ex.getMessage());
//    }
//    @Test
//    public void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() throws Exception{
//        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.of(tuple));
//        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.empty());
//        when(tuple.get("topic",Topic.class)).thenReturn(topic);
//        when(tuple.get("maxId",Long.class)).thenReturn(1L);
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentDto));
//        assertEquals("Resource Account not found for field: email and value: user@user.com", ex.getMessage());
//    }
//    @Test
//    public void shouldRemoveCommentCorrectly() throws Exception{
//        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));
//        when(userEntityRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        doNothing().when(commentRepository).delete(any(Comment.class));
//
//        commentService.removeComment(1L);
//
//        verify(commentRepository, times(1)).delete(any(Comment.class));
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenRemoveCommentOfOtherUserComment() throws Exception{
//        UserEntity userEntity = setCurrentUser();
//        userEntity.setUserId(2L);
//        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));
//        when(userEntityRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(userEntity));
//
//        assertThrows(IllegalResourceModifyException.class, () -> commentService.removeComment(1L));
//    }
//    @Test
//    public void shouldFetchAllCommentsForTopicCorrectly(){
//        when(commentRepository.findAllByTopicIdSortedAsc(anyLong())).thenReturn(List.of(comment1, comment2));
//
//        List<CommentDtoWithUser> comments = commentService.fetchAllCommentsForTopic("ASC", 1L);
//        assertEquals(2, comments.size());
//        assertEquals("Comment", comments.get(0).getText());
//        assertEquals("New Comment", comments.get(1).getText());
//    }
//    @Test
//    public void shouldFetchAllCommentsForUserCorrectly(){
//        when(commentRepository.findByUsername(anyString())).thenReturn(List.of(comment1, comment2));
//
//        List<CommentDtoWithUser> comments = commentService.fetchAllCommentsForUser("user");
//
//        assertEquals(2, comments.size());
//        assertEquals("Comment", comments.get(0).getText());
//        assertEquals("New Comment", comments.get(1).getText());
//    }
//    @Test
//    public void shouldFetchAllCommentsCorrectlyAsc(){
//        when(commentRepository.findAllWithTopicOrderByIdAsc()).thenReturn(List.of(comment1, comment2));
//
//        List<CommentDtoWithUser> comments = commentService.fetchAllComments("ASC");
//
//        assertEquals(2, comments.size());
//        assertEquals("Comment", comments.get(0).getText());
//        assertEquals("New Comment", comments.get(1).getText());
//    }
//    @Test
//    public void shouldFetchAllCommentsCorrectlyDesc(){
//        when(commentRepository.findAllWithTopicOrderByIdDesc()).thenReturn(List.of(comment2, comment1));
//
//        List<CommentDtoWithUser> comments = commentService.fetchAllComments("DESC");
//
//        assertEquals(2, comments.size());
//        assertEquals("Comment", comments.get(1).getText());
//        assertEquals("New Comment", comments.get(0).getText());
//    }
//    @Test
//    public void shouldFetchAllCommentsCorrectlyAscWhenInvalidInputSorting(){
//        when(commentRepository.findAllWithTopicOrderByIdAsc()).thenReturn(List.of(comment1, comment2));
//
//        List<CommentDtoWithUser> comments = commentService.fetchAllComments("X");
//
//        assertEquals(2, comments.size());
//        assertEquals("Comment", comments.get(0).getText());
//        assertEquals("New Comment", comments.get(1).getText());
//    }
//    @Test
//    public void shouldReturnEmptyListWhenNoCommentsFound(){
//        when(commentRepository.findAllWithTopicOrderByIdAsc()).thenReturn(Collections.emptyList());
//
//        List<CommentDtoWithUser> comments = commentService.fetchAllComments("ASC");
//
//        assertEquals(0, comments.size());
//    }
//    @Test
//    public void shouldUpdateCommentCorrectly() throws Exception{
//        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment2));
//        when(userEntityRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
//
//        commentService.updateComment(1L, commentDto);
//        verify(commentRepository).save(captor.capture());
//
//        assertEquals("Comment", captor.getValue().getText());
//    }
//    @Test
//    public void shouldThrowResourceNotFoundExceptionWhenCommentNotFound() throws Exception{
//        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, commentDto));
//    }
//    @Test
//    public void shouldThrowIllegalResourceModifyExceptionWhenUpdatingOtherUserComment() throws Exception{
//        UserEntity userEntity = setCurrentUser();
//        userEntity.setUserId(2L);
//        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));
//        when(userEntityRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(userEntity));
//
//        assertThrows(IllegalResourceModifyException.class, () -> commentService.updateComment(1L, commentDto));
//    }
//
//    private void setAuthentication(){
//        User user = new User("user@user.com","password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//
//    private UserEntity setCurrentUser() {
//        Role role = Role.builder()
//                .roleId(1L)
//                .roleName("USER")
//                .build();
//
//        return UserEntity.builder()
//                .userId(1L)
//                .email("user@user.com")
//                .password("password")
//                .roles(List.of(role))
//                .isEnabled(true)
//                .build();
//    }
//}

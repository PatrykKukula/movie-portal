package pl.patrykkukula.MovieReviewPortal.Service;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.CommentRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CommentServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private Tuple tuple;
    @InjectMocks
    private CommentServiceImpl commentService;

    private Topic topic;
    private CommentDto commentDto;
    private Comment comment1;
    private Comment comment2;
    private UserEntity userEntity;
    private String updateText;

    @BeforeEach
    void setUp(){
        userEntity = setCurrentUser();
        topic = Topic.builder()
                .topicId(1L)
                .title("Topic")
                .comments(Collections.emptyList())
                .entityType("Movie")
                .entityId(1L)
                .user(userEntity)
                .build();
        commentDto = CommentDto.builder()
                .text("Comment")
                .topicId(1L)
                .build();
        comment1 = Comment.builder()
                .commentId(1L)
                .text("Comment1")
                .topic(topic)
                .user(userEntity)
                .build();
        comment2 = Comment.builder()
                .commentId(2L)
                .text("New Comment")
                .topic(topic)
                .user(userEntity)
                .build();
        updateText = "Updated";

    }
    @Test
    public void shouldAddCommentCorrectly(){
        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.of(tuple));
        when(tuple.get("topic",Topic.class)).thenReturn(topic);
        when(tuple.get("maxId",Long.class)).thenReturn(1L);
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        Long commentId = commentService.addComment(commentDto);

        verify(commentRepository).save(captor.capture());
        assertEquals(1L, commentId);
        assertEquals("Comment", captor.getValue().getText());
        assertEquals(2L, captor.getValue().getCommentIdInPost());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenTopicNotFound(){
        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentDto));
        assertEquals("Resource Topic not found for field: topic id and value: 1", ex.getMessage());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenAddCommentAndUserNotFound(){
        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.of(tuple));
        when(tuple.get("topic",Topic.class)).thenReturn(topic);
        when(tuple.get("maxId",Long.class)).thenReturn(1L);
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

       AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> commentService.addComment(commentDto));
        assertEquals("Log in to add comment", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenAddCommentToReply(){
        commentDto.setReply(true);
        commentDto.setReplyCommentId(1L);
        comment1.setReply(true);
        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.of(tuple));
        when(tuple.get("topic",Topic.class)).thenReturn(topic);
        when(tuple.get("maxId",Long.class)).thenReturn(1L);
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment1));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> commentService.addComment(commentDto));
        assertEquals("Comment is reply - you can only reply to the comment that is not a reply", ex.getMessage());
    }
    @Test
    public void shouldThrowDataIntegrityViolationClassWhenAddCommentAndSavingCommentFailed(){
        when(topicRepository.findTopicWithCurrentMaxCommentId(anyLong())).thenReturn(Optional.of(tuple));
        when(tuple.get("topic",Topic.class)).thenReturn(topic);
        when(tuple.get("maxId",Long.class)).thenReturn(1L);
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> commentService.addComment(commentDto));
        assertEquals("Comment could not be added due to concurrency conflict" ,ex.getMessage());
    }
    @Test
    public void shouldRemoveCommentCorrectly(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.of(comment1));
        doNothing().when(commentRepository).delete(any(Comment.class));

        commentService.removeComment(1L, false);

        verify(commentRepository, times(1)).delete(any(Comment.class));
    }
    @Test
    public void shouldRemoveCommentAndRepliesCorrectly(){
        comment2.setReply(true);
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.of(comment1));
        doNothing().when(commentRepository).delete(any(Comment.class));
        when(commentRepository.findAllRepliesByCommentId(anyLong())).thenReturn(List.of(comment2));
        doNothing().when(commentRepository).deleteAll(anyList());

        commentService.removeComment(1L, true);

        verify(commentRepository, times(1)).delete(any(Comment.class));
        verify(commentRepository,times(1)).findAllRepliesByCommentId(1L);
        verifyNoMoreInteractions(commentRepository);
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenRemoveCommentAndCommentDoesNotExist(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.removeComment(1L, false));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenRemoveCommentAndUserNotFound(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> commentService.removeComment(1L, false));
        assertEquals("Log in to add comment", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalResourceModifyExceptionWhenRemoveCommentOfOtherUser(){
        comment1.setUser(UserEntity.builder().userId(999L).build());
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.of(comment1));

        IllegalResourceModifyException ex = assertThrows(IllegalResourceModifyException.class, () -> commentService.removeComment(1L, false));
        assertEquals("You do not have permission to modify this comment", ex.getMessage());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsInvalid(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> commentService.removeComment(-1L, false));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchCommentByIdCorrectly(){
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.of(comment1));
        when(commentRepository.findAllRepliesByCommentId(anyLong())).thenReturn(List.of(comment2));

        CommentDtoWithReplies comment = commentService.fetchCommentById(1L);

        assertEquals(1, comment.getReplies().size());
        assertEquals(1L, comment.getCommentId());
        assertEquals("Comment1", comment.getText());
        assertEquals("New Comment", comment.getReplies().getFirst().getText());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFindCommentByIdAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> commentService.fetchCommentById(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenFindCommentByIdAndCommentNotFound(){
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.fetchCommentById(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldContainNoRepliesWhenFetchCommentByIdAndCommentHasNoReplies(){
        when(commentRepository.findCommentByIdWithUserAndTopic(anyLong())).thenReturn(Optional.of(comment1));
        when(commentRepository.findAllRepliesByCommentId(anyLong())).thenReturn(Collections.emptyList());

        CommentDtoWithReplies comment = commentService.fetchCommentById(1L);

        assertEquals(0, comment.getReplies().size());
    }
    @Test
    public void shouldFetchAllCommentsForUserCorrectly(){
        when(commentRepository.findAllCommentsForUserByUsername(anyString())).thenReturn(List.of(comment1, comment2));

        List<CommentDtoWithUser> comments = commentService.fetchAllCommentsForUser("User");

        assertEquals(2, comments.size());
        assertEquals("Comment1", comments.getFirst().getText());
        assertEquals("New Comment", comments.get(1).getText());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchAllCommentsForUserAndUserDoesNotHaveAnyComment(){
        when(commentRepository.findAllCommentsForUserByUsername(anyString())).thenReturn(Collections.emptyList());

        List<CommentDtoWithUser> comments = commentService.fetchAllCommentsForUser("User");

        assertEquals(0, comments.size());
    }
    @Test
    public void shouldUpdateCommentVaadinCorrectly(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        commentService.updateCommentVaadin(1L, commentDto);
        verify(commentRepository).save(captor.capture());
        Comment updatedComment = captor.getValue();

        assertEquals("Comment", updatedComment.getText());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenUpdateCommentVaadinAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> commentService.updateCommentVaadin(null, commentDto));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenUpdateCommentVaadinAndUserNotLoggedIn(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> commentService.updateCommentVaadin(1L, commentDto));
        assertEquals("Log in to add comment", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenUpdateCommentVaadinAndCommentNotFound(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.updateCommentVaadin(1L, commentDto));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowIllegalResourceModifyExceptionWhenUpdateCommentVaadinOfOtherUser(){
        comment1.setUser(UserEntity.builder().userId(999L).build());
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));

        IllegalResourceModifyException ex = assertThrows(IllegalResourceModifyException.class, () -> commentService.updateCommentVaadin(1L, commentDto));
        assertEquals("You do not have permission to modify this comment", ex.getMessage());
    }
    @Test
    public void shouldUpdateCommentCorrectly(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        commentService.updateComment(1L, updateText);
        verify(commentRepository).save(captor.capture());
        Comment updatedComment = captor.getValue();

        assertEquals("Updated", updatedComment.getText());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenUpdateCommentAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> commentService.updateComment(null, updateText));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenUpdateCommentAndUserNotLoggedIn(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> commentService.updateComment(1L, updateText));
        assertEquals("Log in to add comment", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenUpdateCommentAndCommentNotFound(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, updateText));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowIllegalResourceModifyExceptionWhenUpdateCommentOfOtherUser(){
        comment1.setUser(UserEntity.builder().userId(999L).build());
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(userEntity));
        when(commentRepository.findCommentByIdWithUser(anyLong())).thenReturn(Optional.of(comment1));

        IllegalResourceModifyException ex = assertThrows(IllegalResourceModifyException.class, () -> commentService.updateComment(1L, updateText));
        assertEquals("You do not have permission to modify this comment", ex.getMessage());
    }
    
    private UserEntity setCurrentUser() {
        Role role = Role.builder()
                .roleId(1L)
                .roleName("USER")
                .build();

        return UserEntity.builder()
                .userId(1L)
                .roles(List.of(role))
                .username("User")
                .isEnabled(true)
                .build();
    }
}

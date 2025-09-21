package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CommentServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CommentServiceImpl commentService;
    @Autowired
    private ObjectMapper mapper;

    private CommentDto commentDto;
    private CommentDtoWithUser commentDtoWithUser;
    private CommentDtoWithReplies commentDtoWithReplies;

    @BeforeEach
    public void setUp() throws Exception {
        commentDto = CommentDto.builder()
                .text("Comment")
                .topicId(1L)
                .isReply(false)
                .replyCommentId(null)
                .build();
        commentDtoWithUser = CommentDtoWithUser.builder()
                .commentId(1L)
                .text("Comment")
                .topicId(1L)
                .commentIdInPost(1L)
                .isReply(true)
                .repliedCommentId(1L)
                .build();
        commentDtoWithReplies = CommentDtoWithReplies.builder()
                .commentId(1L)
                .text("comment")
                .author("user")
                .build();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN enter authenticated endpoint")
    public void shouldAllowAdminEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", 1L)).andExpect(status().isAccepted());
        verify(commentService, times(1)).removeComment(anyLong(), anyBoolean());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow MODERATOR enter authenticated endpoint")
    public void shouldAllowModeratorEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", 1L)).andExpect(status().isAccepted());
        verify(commentService, times(1)).removeComment(anyLong(), anyBoolean());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow USER enter authenticated endpoint")
    public void shouldAllowUserEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", 1L)).andExpect(status().isAccepted());
        verify(commentService, times(1)).removeComment(anyLong(), anyBoolean());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should deny ANONYMOUS enter authenticated endpoint")
    public void shouldDenyAnonymousEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", 1L)).andExpect(status().isUnauthorized());
        verifyNoInteractions(commentService);
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow ANONYMOUS enter public endpoint")
    public void shouldAllowAnonymousEnterPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/comments/{id}", 1L)).andExpect(status().isOk());
        verify(commentService, times(1)).fetchCommentById(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create comment correctly")
    public void shouldCreateCommentCorrectly() throws Exception {
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(status().isCreated(),
                        header().string("Location", containsString("/comments")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                        );
        verify(commentService, times(1)).addComment(any(CommentDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when create comment and text exceed character limit")
    public void shouldRespond400WhenCreateCommentAndTextExceedCharacterLimit() throws Exception {
        commentDto.setText("a".repeat(1001));
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Title must not exceed 1000 characters"))
                );
        verifyNoInteractions(commentService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 404 when create comment and service throws ResourceNotFoundException")
    public void shouldRespond404WhenCreateCommentAndServiceThrowsResourceNotFoundException() throws Exception {
        when(commentService.addComment(any(CommentDto.class))).thenThrow(new ResourceNotFoundException("topic", "topic", "1"));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(commentService, times(1)).addComment(any(CommentDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when create comment and service throws IllegalStateException")
    public void shouldRespond400WhenCreateCommentAndServiceThrowsIllegalStateException() throws Exception {
        when(commentService.addComment(any(CommentDto.class))).thenThrow(new IllegalStateException("Comment is reply - you can only reply to the comment that is not a reply"));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("Comment is reply - you can only reply to the comment that is not a reply")
                );
        verify(commentService, times(1)).addComment(any(CommentDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should delete comment correctly")
    public void shouldDeleteCommentCorrectly() throws Exception {
        mockMvc.perform(delete("/api/comments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(commentService, times(1)).removeComment(anyLong(), anyBoolean());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when delete comment and ID is invalid")
    public void shouldRespond400WhenDeleteCommentAndIdIsInvalid() throws Exception {
        doThrow(new InvalidIdException()).when(commentService).removeComment(anyLong(), anyBoolean());

        mockMvc.perform(delete("/api/comments/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(commentService, times(1)).removeComment(anyLong(), anyBoolean());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when delete comment of other user")
    public void shouldRespond400WhenDeleteCommentOfOtherUser() throws Exception {
        doThrow(new IllegalResourceModifyException("You do not have permission to modify this comment"))
                .when(commentService).removeComment(anyLong(), anyBoolean());

        mockMvc.perform(delete("/api/comments/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("You do not have permission to modify this comment")
                );
        verify(commentService, times(1)).removeComment(anyLong(), anyBoolean());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should update comment correctly")
    public void shouldUpdateCommentCorrectly() throws Exception {
        mockMvc.perform(patch("/api/comments/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("update"))
                .andExpectAll(status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(commentService, times(1)).updateComment(anyLong(), anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 500 when update comment with no text")
    public void shouldRespond500WhenUpdateCommentWithNoText() throws Exception {
        mockMvc.perform(patch("/api/comments/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isInternalServerError(),
                        jsonPath("$.statusCode").value("500")
                );
        verifyNoInteractions(commentService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should fetch comment by ID correctly")
    public void shouldFetchCommentByIdCorrectly() throws Exception {
        when(commentService.fetchCommentById(anyLong())).thenReturn(commentDtoWithReplies);

        mockMvc.perform(get("/api/comments/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.commentId").value(1),
                        jsonPath("$.text").value("comment"),
                        jsonPath("$.author").value("user")
                );
        verify(commentService, times(1)).fetchCommentById(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 404 when fetch comment by ID and service throws ResourceNotFoundException")
    public void shouldRespond404WhenFetchCommentByIdAndServiceThrowsResourceNotFoundException() throws Exception {
        when(commentService.fetchCommentById(anyLong())).thenThrow(new ResourceNotFoundException("comment", "comment", "1"));

        mockMvc.perform(get("/api/comments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(commentService, times(1)).fetchCommentById(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when fetch comment by ID and service throws InvalidIdException")
    public void shouldRespond400WhenFetchCommentByIdAndServiceThrowsInvalidIdException() throws Exception {
        when(commentService.fetchCommentById(anyLong())).thenThrow(new InvalidIdException());

        mockMvc.perform(get("/api/comments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("ID cannot be less than 1 or null"))
                );
        verify(commentService, times(1)).fetchCommentById(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should fetch all comments for user correctly")
    public void shouldFetchAllCommentForUserCorrectly() throws Exception {
        when(commentService.fetchAllCommentsForUser(anyString())).thenReturn(List.of(commentDtoWithUser));

        mockMvc.perform(get("/api/comments/user/username")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0].text").value("Comment"),
                        jsonPath("$.[0].reply").value(true)
                );
        verify(commentService, times(1)).fetchAllCommentsForUser(anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should fetch all comments for user correctly")
    public void shouldReturnEmptyListWhenFetchAllCommentForUser() throws Exception {
        when(commentService.fetchAllCommentsForUser(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/comments/user/username")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0),
                        content().string(containsString("[]"))
                );
        verify(commentService, times(1)).fetchAllCommentsForUser(anyString());
    }
}

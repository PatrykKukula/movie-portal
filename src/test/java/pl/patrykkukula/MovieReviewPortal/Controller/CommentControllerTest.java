package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import pl.patrykkukula.MovieReviewPortal.Dto.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Service.CommentServiceImpl;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
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


    @BeforeEach
    public void setUp() throws Exception {
        commentDto = CommentDto.builder()
                .text("Comment")
                .topicId(1L)
                .commentIdInPost(1L)
                .build();
        commentDtoWithUser = CommentDtoWithUser.builder()
                .text("Comment")
                .topicId(1L)
                .commentIdInPost(1L)
                .user("User")
                .build();
    }

    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldCreateCommentCorrectly() throws Exception {
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/comments")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldRespond401WhenCreateCommentWithAnonymousUser() throws Exception {
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().string(containsString("Unauthorized"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond400WhenCreateCommentWithInvalidRequestBody() throws Exception {
        commentDto.setTopicId(-1L);
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("topicId: Topic ID must be greater than 0")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldDeleteCommentCorrectly() throws Exception {
        mockMvc.perform(delete("/comments/{commentId}", 1L))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond400WhenDeleteCommentOfOtherUser() throws Exception {
        doThrow(new IllegalResourceModifyException("You do not have permission to delete this comment"))
                .when(commentService).removeComment(anyLong());

        mockMvc.perform(delete("/comments/{commentId}", 1L))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad request"),
                        jsonPath("$.errorMessage").value("You do not have permission to delete this comment")
                );
    }
    @Test
    public void shouldFetchAllCommentsForTopicCorrectly() throws Exception {
        when(commentService.fetchAllCommentsForTopic(anyString(), anyLong())).thenReturn(List.of(commentDtoWithUser));

        mockMvc.perform(get("/comments/topic/{topicId}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.[0].text").value("Comment"),
                        jsonPath("$.[0].commentIdInPost").value(1),
                        jsonPath("$.[0].user").value("User")
                );
    }
    @Test
    public void shouldRespond404WhenFetchAllCommentsForTopicAndInvalidTopicId() throws Exception {
        doThrow(InvalidIdException.class)
                .when(commentService).fetchAllCommentsForTopic(anyString(), anyLong());

        mockMvc.perform(get("/comments/topic/{topicId}", 0L))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad request")
                );
    }
    @Test
    public void shouldFetchAllCommentsForUserCorrectly() throws Exception {
        when(commentService.fetchAllCommentsForUser(anyString())).thenReturn(List.of(commentDtoWithUser));

        mockMvc.perform(get("/comments/user/{username}", "username"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.[0].text").value("Comment"),
                        jsonPath("$.[0].commentIdInPost").value(1),
                        jsonPath("$.[0].user").value("User")
                );
    }
    @Test
    public void shouldFetchAllCommentsCorrectlyDesc() throws Exception {
        CommentDtoWithUser commentDtoWithUser1 = CommentDtoWithUser.builder()
                .text("New Comment")
                .topicId(2L)
                .commentIdInPost(2L)
                .user("New User")
                .build();
        when(commentService.fetchAllComments(anyString()))
                .thenReturn(List.of(commentDtoWithUser, commentDtoWithUser1));

        mockMvc.perform(get("/comments")
                        .param("sorted","DESC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(2),
                        jsonPath("$.[0].text").value("Comment"),
                        jsonPath("$.[0].commentIdInPost").value(1),
                        jsonPath("$.[0].user").value("User"),
                        jsonPath("$.[1].text").value("New Comment"),
                        jsonPath("$.[1].commentIdInPost").value(2),
                        jsonPath("$.[1].user").value("New User")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldUpdateCommentCorrectly() throws Exception {
        mockMvc.perform(patch("/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond400WhenUpdateCommentWithInvalidRequestBody() throws Exception {
        commentDto.setTopicId(-1L);

        mockMvc.perform(patch("/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("topicId: Topic ID must be greater than 0")
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldRespond401WhenUpdateCommentWithAnonymousUser() throws Exception {

        mockMvc.perform(patch("/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpectAll(
                        status().isUnauthorized(),
                       content().string(containsString("Unauthorized"))
                );
    }
}

package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.*;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class TopicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TopicServiceImpl topicService;
    @Autowired
    private ObjectMapper mapper;

    private TopicDtoWithCommentDto topicDtoWithComment;
    private TopicDtoToDisplay topicDtoToDisplay;
    private TopicDtoBasic topicDtoBasic;

    @BeforeEach
    void setUp() {
        TopicDto topicDto = TopicDto.builder()
                .title("Topic")
                .entityId(1L)
                .entityType("Movie")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .topicId(1L)
                .isReply(false)
                .replyCommentId(null)
                .build();
        topicDtoWithComment = TopicDtoWithCommentDto.builder()
                .topic(topicDto)
                .comment(commentDto)
                .build();
        topicDtoToDisplay = TopicDtoToDisplay.builder()
                .topicId(1L)
                .title("Topic")
                .author("Author")
                .comments(Collections.emptyList())
                .postCount(1L)
                .build();
        topicDtoBasic = TopicDtoBasic.builder()
                .id(1L)
                .title("Topic")
                .createdBy("User")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .postCount(1L)
                .build();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN enter authenticated endpoint")
    public void shouldAllowAdminEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment))).andExpect(status().isCreated());
        verify(topicService, times(1)).createTopic(any(TopicDtoWithCommentDto.class), anyLong(), anyString());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow MODERATOR enter authenticated endpoint")
    public void shouldAllowModeratorEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment))).andExpect(status().isCreated());
        verify(topicService, times(1)).createTopic(any(TopicDtoWithCommentDto.class), anyLong(), anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should allow USER enter authenticated endpoint")
    public void shouldAllowUserEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment))).andExpect(status().isCreated());
        verify(topicService, times(1)).createTopic(any(TopicDtoWithCommentDto.class), anyLong(), anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should deny ANONYMOUS enter authenticated endpoint")
    public void shouldDenyAnonymousUserEnterAuthenticatedEndpoint() throws Exception {
        mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment))).andExpect(status().isUnauthorized());
        verifyNoInteractions(topicService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should allow ANONYMOUS enter public endpoint")
    public void shouldAllowAnonymousEnterPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/topics/latest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment))).andExpect(status().isOk());
        verify(topicService, times(1)).fetchLatestTopics();
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should create topic correctly")
    public void shouldCreateTopicCorrectly() throws Exception {
        mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/topics")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
        verify(topicService,times(1)).createTopic(any(TopicDtoWithCommentDto.class), anyLong(), anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when create topic and topic dto is null")
    public void shouldRespond400WhenCreateTopicAndTopicDtoIsNull() throws Exception {
        topicDtoWithComment.setTopic(null);
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Topic cannot be null or empty"))
                );
        verifyNoInteractions(topicService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when create topic and topic title is empty")
    public void shouldRespond400WhenCreateTopicAndTopicTitleIsEmpty() throws Exception {
        topicDtoWithComment.setTopic(TopicDto.builder().entityType("Movie").entityId(1L).build());
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Title cannot empty"))
                );
        verifyNoInteractions(topicService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when create topic and title exceed character limit")
    public void shouldRespond400WhenCreateTopicAndTopicTitleExceedCharacterLimit() throws Exception {
        topicDtoWithComment.setTopic(TopicDto.builder().title("a".repeat(256)).entityType("Movie").entityId(1L).build());
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Title must not exceed 255 characters"))
                );
        verifyNoInteractions(topicService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when create topic and entity id is less than one")
    public void shouldRespond400WhenCreateTopicAndEntityIdIsLessThanOne() throws Exception {
        topicDtoWithComment.setTopic(TopicDto.builder().title("title").entityType("Movie").entityId(-1L).build());
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Entity id cannot be less than 1"))
                );
        verifyNoInteractions(topicService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 404 when create topic and service throws ResourceNotFoundException")
    public void shouldRespond404WhenCreateTopicAndServiceThrowsResourceNotFoundException() throws Exception {
        when(topicService.createTopic(any(TopicDtoWithCommentDto.class), anyLong(), anyString())).thenThrow(new ResourceNotFoundException("movie", "ID", "1"));
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(topicService, times(1)).createTopic(any(TopicDtoWithCommentDto.class), anyLong(), anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should delete topic correctly")
    public void shouldDeleteTopicCorrectly() throws Exception {
        mockMvc.perform(delete("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value(containsString("Accepted"))
                );
        verify(topicService, times(1)).deleteTopic(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 404 when delete topic and service throws ResourceNotFoundException")
    public void shouldRespond404WhenDeleteTopicAndServiceThrowsResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Topic", "topic", "1")).when(topicService).deleteTopic(anyLong());

        mockMvc.perform(delete("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(topicService, times(1)).deleteTopic(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when delete topic and service throws IllegalResourceModifyException")
    public void shouldRespond400WhenDeleteTopicAndServiceThrowsIllegalResourceModifyException() throws Exception {
        doThrow(new IllegalResourceModifyException("You are not author of this topic")).when(topicService).deleteTopic(anyLong());

        mockMvc.perform(delete("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("You are not author of this topic")
                );
        verify(topicService, times(1)).deleteTopic(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should find topic by ID correctly")
    public void shouldFindTopicByIdCorrectly() throws Exception {
        when(topicService.findTopicById(anyLong())).thenReturn(topicDtoToDisplay);

        mockMvc.perform(get("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("Topic"),
                        jsonPath("$.author").value("Author"),
                        jsonPath("$.comments.size()").value(0)
                );
        verify(topicService, times(1)).findTopicById(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 404 when find topic by id and service throws ResourceNotFoundException")
    public void shouldRespond404WhenFindTopicByIdAndServiceThrowsResourceNotFoundException() throws Exception {
        when(topicService.findTopicById(anyLong())).thenThrow(new ResourceNotFoundException("topic", "topic", "1"));

        mockMvc.perform(get("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(topicService, times(1)).findTopicById(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should find all topics correctly with no input parameters")
    public void shouldFindAllTopicsCorrectlyWithNoInputParameters() throws Exception {
        when(topicService.findAllTopics(anyInt(), anyInt(), anyString(), anyString(), anyLong())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(topicService, times(1)).findAllTopics(anyInt(), anyInt(), anyString(), anyString(), anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should find all topics correctly with input parameters")
    public void shouldFindAllTopicsCorrectlyWithInputParameters() throws Exception {
        when(topicService.findAllTopics(anyInt(), anyInt(), anyString(), anyString(), anyLong()))
                .thenReturn(new PageImpl<>(List.of(topicDtoBasic), PageRequest.of(0, 1), 1));

        mockMvc.perform(get("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sorted", "ASC")
                        .param("page", "0")
                        .param("page-size", "10")
                        .param("entity-type", "movie")
                        .param("entity-id", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(topicService, times(1)).findAllTopics(anyInt(), anyInt(), anyString(), anyString(), anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should find topics by title correctly")
    public void shouldFindTopicsByTitleCorrectly() throws Exception {
        when(topicService.findTopicsByTitle(anyString(), anyString())).thenReturn(List.of(topicDtoBasic));

        mockMvc.perform(get("/api/topics/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "title")
                        .param("sorted", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(topicService, times(1)).findTopicsByTitle(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should find topics by title correctly with no sorted param")
    public void shouldFindTopicsByTitleCorrectlyWithNoSortedParam() throws Exception {
        when(topicService.findTopicsByTitle(anyString(), anyString())).thenReturn(List.of(topicDtoBasic));

        mockMvc.perform(get("/api/topics/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "title"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(topicService, times(1)).findTopicsByTitle(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should update topic correctly")
    public void shouldUpdateTopicCorrectly() throws Exception {
        mockMvc.perform(patch("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TopicUpdateDto("title"))))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(topicService, times(1)).updateTopic(anyLong(), any(TopicUpdateDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when update topic and title exceed character limit")
    public void shouldRespond400WhenUpdateTopicAndTitleExceedCharacterLimit() throws Exception {
        mockMvc.perform(patch("/api/topics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TopicUpdateDto("a".repeat(256)))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Title must not exceed 255 characters"))
                );
        verifyNoInteractions(topicService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 404 when update topic and service throws ResourceNotFoundException")
    public void shouldRespond404WhenUpdateTopicAndServiceThrowsResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("topic", "topic", "1")).when(topicService).updateTopic(anyLong(), any(TopicUpdateDto.class));

        mockMvc.perform(patch("/api/topics/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TopicUpdateDto("title"))))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(topicService, times(1)).updateTopic(anyLong(), any(TopicUpdateDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should find latest topics correctly")
    public void shouldFindLatestTopicsCorrectly() throws Exception {
        when(topicService.fetchLatestTopics()).thenReturn(List.of(MainViewTopicDto.builder().title("title").build()));

        mockMvc.perform(get("/api/topics/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("1"),
                        jsonPath("$.[0].title").value("title")
                );
        verify(topicService, times(1)).fetchLatestTopics();
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when find latest topics and no topics found")
    public void shouldReturnEmptyListWhenFindLatestTopicsAndNoTopicsFound() throws Exception {
        when(topicService.fetchLatestTopics()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/topics/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("0")
                );
        verify(topicService, times(1)).fetchLatestTopics();
    }
}

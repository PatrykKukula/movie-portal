package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoWithCommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;

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

//    @BeforeEach
//    void setUp() {
//        TopicDto topicDto = TopicDto.builder()
//                .title("Topic")
//                .movieId(1L)
//                .build();
//        CommentDto commentDto = CommentDto.builder()
//                .text("Comment")
//                .topicId(1L)
//                .build();
//        topicDtoWithComment = TopicDtoWithCommentDto.builder()
//                .topic(topicDto)
//                .comment(commentDto)
//                .build();
//        topicDtoToDisplay = TopicDtoToDisplay.builder()
//                .topicId(1L)
//                .title("Topic")
//                .movieTitle("Movie")
//                .author("Author")
//                .comments(Collections.emptyList())
//                .postCount(1L)
//                .build();
//        topicDtoBasic = TopicDtoBasic.builder()
//                .title("Topic")
//                .author("Author")
//                .movieTitle("Movie")
//                .postCount(1L)
//                .build();
//    }


    @Test
    @WithMockUser(roles = "USER")
    public void shouldCreateTopicCorrectly() throws Exception {
        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/topics")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldRespond401WhenAddTopicWithUserAnonymous() throws Exception {
        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().string(containsString("Unauthorized"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond400WhenAddTopicWithInvalidRequestBody() throws Exception {
        topicDtoWithComment.setTopic(null);
        mockMvc.perform(post("/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(topicDtoWithComment)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value(("topic: Topic cannot be null"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldDeleteTopicCorrectly() throws Exception {
        mockMvc.perform(delete("/topics/{topicId}", 1L))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value(202),
                        jsonPath("$.statusMessage").value(("Accepted"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond400WhenDeletingOtherUserTopic() throws Exception {
        doThrow((new IllegalResourceModifyException("You are not author of this topic"))).when(topicService).deleteTopic(1L);

        mockMvc.perform(delete("/topics/{topicId}", 1L))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value(("You are not author of this topic"))
                );
    }
    @Test
    public void shouldFindTopicByIdCorrectly() throws Exception {
        when(topicService.findTopicById(anyLong())).thenReturn(topicDtoToDisplay);

        mockMvc.perform(get("/topics/{topicId}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("Topic"),
                        jsonPath("$.author").value(("Author")),
                        jsonPath("$.comments.length()").value(0)
                );
    }
    @Test
    public void shouldRespond404WhenNoTopicFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(topicService).findTopicById(anyLong());

        mockMvc.perform(get("/topics/{topicId}", 1L))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404")
                );
    }
//    @Test
//    public void shouldFindAllTopicsCorrectly() throws Exception {
//        when(topicService.findAllTopics(0, 10, anyString())).thenReturn(List.of(topicDtoBasic));
//
//        mockMvc.perform(get("/topics"))
//                .andExpectAll(
//                        status().isOk(),
//                        jsonPath("$.[0].title").value("Topic"),
//                        jsonPath("$.[0].postCount").value(1)
//                );
//    }
    @Test
    public void shouldFindTopicsByTitleCorrectly() throws Exception {
        when(topicService.findTopicsByTitle(anyString(), anyString())).thenReturn(List.of(topicDtoBasic));

        mockMvc.perform(get("/topics/search")
                        .param("title", "Topic"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.[0].title").value("Topic"),
                        jsonPath("$.[0].postCount").value(1)
                );
    }
    @Test
    public void shouldReturnEmptyListTopicNotFoundByTitle() throws Exception {
        when(topicService.findTopicsByTitle(anyString(),anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/topics/search")
                        .param("title", "Topic"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }
    @Test
    @WithMockUser("User")
    public void shouldUpdateTopicCorrectly() throws Exception {
        doNothing().when(topicService).updateTopic(anyLong(),Mockito.any(TopicUpdateDto.class));

        mockMvc.perform(patch("/topics/{topicId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TopicUpdateDto("Title"))))
                        .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser("User")
    public void shouldRespond400WhenUpdateTopicWithInvalidRequestBody() throws Exception {
        mockMvc.perform(patch("/topics/{topicId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TopicUpdateDto(null))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("title: Title cannot be null or empty")
                );
    }
    @Test
    @WithMockUser("User")
    public void shouldRespond400WhenUpdateTopicOfOtherUser() throws Exception {
        doThrow(new IllegalResourceModifyException("You are not author of this topic")).when(topicService).updateTopic(anyLong(),Mockito.any(TopicUpdateDto.class));

        mockMvc.perform(patch("/topics/{topicId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new TopicUpdateDto("Topic"))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("You are not author of this topic")
                );
    }
}

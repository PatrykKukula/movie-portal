package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.BanDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserServiceImpl userService;
    @Autowired
    private ObjectMapper mapper;
    private Pageable pageable;

    @BeforeEach
    public void setUp(){
        pageable = PageRequest.of(0, 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN enter ADMIN endpoints")
    public void shouldAllowAdminEnterAdminEndpoints() throws Exception {
        when(userService.removeRole(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/user/remove-role")
                        .param("username", "user")
                        .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(userService, times(1)).removeRole(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should deny MODERATOR enter ADMIN endpoints")
    public void shouldDenyModeratorEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/user/remove-role")
                .param("username", "user")
                .param("role", "MODERATOR")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verifyNoInteractions(userService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny USER enter ADMIN endpoints")
    public void shouldDenyUserEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/user/remove-role")
                .param("username", "user")
                .param("role", "USER")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verifyNoInteractions(userService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should deny ANONYMOUS enter ADMIN endpoints")
    public void shouldDenyAnonymousEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/user/remove-role")
                .param("username", "user")
                .param("role", "USER")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        verifyNoInteractions(userService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should allow ANONYMOUS enter GET endpoints")
    public void shouldAllowAnonymousEnterGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/user/{id}", 1L)
                .param("username", "user")
                .param("role", "USER")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(userService, times(1)).loadUserEntityById(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find user by ID correctly")
    public void shouldFindUserByIdCorrectly() throws Exception {
        when(userService.loadUserEntityById(anyLong())).thenReturn(UserDataDto.builder().userId(1L).username("user").build());

        mockMvc.perform(get("/api/user/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                  status().isOk(),
                  jsonPath("$.userId").value(1),
                  jsonPath("$.username").value("user")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 404 when find user by ID and service throws ResourceNotFoundException")
    public void shouldRespond404WhenFindUserByIdAndServiceThrowsResourceNotFoundException() throws Exception {
        when(userService.loadUserEntityById(anyLong())).thenThrow(new ResourceNotFoundException("user", "user", "1"));

        mockMvc.perform(get("/api/user/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(Matchers.containsString("not found")));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 404 when find user by ID and service throws ResourceNotFoundException")
    public void shouldRespond400WhenFindUserByIdAndServiceThrowsInvalidIdException() throws Exception {
        when(userService.loadUserEntityById(anyLong())).thenThrow(new InvalidIdException());

        mockMvc.perform(get("/api/user/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should ban user correctly")
    public void shouldBanUserCorrectly() throws Exception {
        when(userService.banUser(any(BanDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/user/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BanDto("username", "7 days"))))
                .andExpectAll(
                        status().isOk(),
                        content().string("User banned successfully")
                );
        verify(userService, times(1)).banUser(any(BanDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when ban user and user not found")
    public void shouldRespond400WhenBanUserAndUserNotFound() throws Exception {
        when(userService.banUser(any(BanDto.class))).thenReturn(false);

        mockMvc.perform(post("/api/user/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BanDto("username", "7 days"))))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("Failed to ban user. Please try again")
                );
        verify(userService, times(1)).banUser(any(BanDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when ban user and service throws IllegalStateException")
    public void shouldRespond400WhenBanUserAndServiceThrowsIllegalStateException() throws Exception {
        when(userService.banUser(any(BanDto.class))).thenThrow(new IllegalStateException("Choose valid ban duration: 24 hours, 7 days, 30 days or permanent"));

        mockMvc.perform(post("/api/user/ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BanDto("username", "777 days"))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("Choose valid ban duration: 24 hours, 7 days, 30 days or permanent")
                );
        verify(userService, times(1)).banUser(any(BanDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should remove ban correctly")
    public void shouldRemoveBanCorrectly() throws Exception {
        when(userService.removeBan(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/user/remove-ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Removed ban from user successfully")
                );
        verify(userService, times(1)).removeBan(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 404 when remove ban and service throws ResourceNotFoundException")
    public void shouldRespond404WhenRemoveBanAndServiceThrowsResourceNotFoundException() throws Exception {
        when(userService.removeBan(anyString())).thenThrow(new ResourceNotFoundException("user", "user", "user"));

        mockMvc.perform(post("/api/user/remove-ban")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(Matchers.containsString("not found"))
                );
        verify(userService, times(1)).removeBan(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add role to user correctly")
    public void shouldAddRoleToUserCorrectly() throws Exception {
        when(userService.addRole(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/user/add-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user")
                        .param("role", "MODERATOR"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Role MODERATOR added successfully")
                );
        verify(userService, times(1)).addRole(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add role to user and service throws IllegalStatException")
    public void shouldRespond400WhenAddRoleToUseAndServiceThrowsIllegalStateException() throws Exception {
        when(userService.addRole(anyString(), anyString())).thenThrow(new IllegalStateException("Choose valid role: MODERATOR"));

        mockMvc.perform(post("/api/user/add-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user")
                        .param("role", "X"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("Choose valid role: MODERATOR")
                );
        verify(userService, times(1)).addRole(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add role to user correctly")
    public void shouldRemoveRoleFromUserCorrectly() throws Exception {
        when(userService.removeRole(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/user/remove-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user")
                        .param("role", "MODERATOR"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Role MODERATOR removed successfully")
                );
        verify(userService, times(1)).removeRole(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 500 when remove role from user and service throws RuntimeException")
    public void shouldRespond500WhenRemoveRoleFromUserAndServiceThrowsRuntimeException() throws Exception {
        when(userService.removeRole(anyString(), anyString())).thenThrow(new RuntimeException("Role USER not found. Please contact technical support"));

        mockMvc.perform(delete("/api/user/remove-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user")
                        .param("role", "MODERATOR"))
                .andExpectAll(
                        status().isInternalServerError(),
                        jsonPath("$.statusCode").value("500"),
                        jsonPath("$.errorMessage").value("Role USER not found. Please contact technical support")
                );
        verify(userService, times(1)).removeRole(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when remove role from user and service throws IllegalStateException")
    public void shouldRespond400WhenRemoveRoleFromUserAndServiceThrowsIllegalStateException() throws Exception {
        when(userService.removeRole(anyString(), anyString())).thenThrow(
                new IllegalStateException("You can only remove MODERATOR role. If you tried to remove MODERATOR role and see this response, user doesn't have that role"));

        mockMvc.perform(delete("/api/user/remove-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "user")
                        .param("role", "MODERATOR"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage")
                                .value("You can only remove MODERATOR role. If you tried to remove MODERATOR role and see this response, user doesn't have that role")
                );
        verify(userService, times(1)).removeRole(anyString(), anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should count users correctly")
    public void shouldCountRegisteredUsersCorrectly() throws Exception {
        when(userService.countUsers(any())).thenReturn(999);

        mockMvc.perform(get("/api/user/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().string("999")
                );
        verify(userService, times(1)).countUsers(any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find all users correctly")
    public void shouldFindAllUsersCorrectly() throws Exception {
        when(userService.fetchAllUsers(anyInt(), anyInt(), any(Sort.class), any()))
                .thenReturn(new PageImpl<>(List.of(UserDataDto.builder().userId(1L).build()), pageable, 1));

        mockMvc.perform(get("/api/user/find-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNo", "0")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0].userId").value(1)
                );
        verify(userService, times(1)).fetchAllUsers(anyInt(), anyInt(), any(Sort.class), any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty page when find all users and no users found")
    public void shouldReturnEmptyPageWhenFindAllUsersAndNoUsersFound() throws Exception {
        when(userService.fetchAllUsers(anyInt(), anyInt(), any(Sort.class), any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/user/find-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNo", "0")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchAllUsers(anyInt(), anyInt(), any(Sort.class), any());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find average movie rate for user by ID")
    public void shouldFindAverageMovieRateForUserById() throws Exception {
        when(userService.fetchAverageRate(anyLong())).thenReturn(5.0);

        mockMvc.perform(get("/api/user/average-movie-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().string(Matchers.containsString("5.0"))
                );
        verify(userService, times(1)).fetchAverageRate(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when find average movie rate for user by ID and ID is invalid")
    public void shouldRespond400WhenFindAverageMovieRateForUserByIdAndServiceThrowsInvalidIdException() throws Exception {
        when(userService.fetchAverageRate(anyLong())).thenThrow(new InvalidIdException());

        mockMvc.perform(get("/api/user/average-movie-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "-1"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(userService, times(1)).fetchAverageRate(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find most rated movie category by user")
    public void shouldFindMostRatedMovieCategoryByUser() throws Exception {
        when(userService.fetchMostRatedCategory(anyLong())).thenReturn(MovieCategory.ACTION);

        mockMvc.perform(get("/api/user/most-rated-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().string("\"ACTION\"")
                );
        verify(userService, times(1)).fetchMostRatedCategory(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find highest rated movies category by user")
    public void shouldFindHighestRatedMoviesForUserCorrectly() throws Exception {
        when(userService.fetchHighestRatedMoviesByUser(anyLong())).thenReturn(List.of(MovieDtoWithUserRate.builder().id(1L).build()));

        mockMvc.perform(get("/api/user/highest-rated-movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0]id").value(1)
                );
        verify(userService, times(1)).fetchHighestRatedMoviesByUser(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when find highest rated movies for user")
    public void shouldReturnEmptyListWhenFindHighestRatedMoviesForUser() throws Exception {
        when(userService.fetchHighestRatedMoviesByUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/user/highest-rated-movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchHighestRatedMoviesByUser(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find highest rated actors for user correctly")
    public void shouldFindHighestRatedActorsForUserCorrectly() throws Exception {
        when(userService.fetchHighestRatedActorsByUser(anyLong())).thenReturn(List.of(ActorDtoWithUserRate.builder().id(1L).build()));

        mockMvc.perform(get("/api/user/highest-rated-actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0]id").value(1)
                );
        verify(userService, times(1)).fetchHighestRatedActorsByUser(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when find highest rated actors for user")
    public void shouldReturnEmptyListWhenFindHighestRatedActorsForUser() throws Exception {
        when(userService.fetchHighestRatedActorsByUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/user/highest-rated-actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchHighestRatedActorsByUser(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find highest rated directors for user correctly")
    public void shouldFindHighestRatedDirectorsForUserCorrectly() throws Exception {
        when(userService.fetchHighestRatedDirectorsByUser(anyLong())).thenReturn(List.of(DirectorDtoWithUserRate.builder().id(1L).build()));

        mockMvc.perform(get("/api/user/highest-rated-directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0]id").value(1)
                );
        verify(userService, times(1)).fetchHighestRatedDirectorsByUser(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when find highest rated directors for user")
    public void shouldReturnEmptyListWhenFindHighestRatedDirectorsForUser() throws Exception {
        when(userService.fetchHighestRatedDirectorsByUser(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/user/highest-rated-directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchHighestRatedDirectorsByUser(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should count movies rated by user correctly")
    public void shouldCountMoviesRatedByUserCorrectly() throws Exception {
        when(userService.fetchMovieRateCount(anyLong())).thenReturn(999L);

        mockMvc.perform(get("/api/user/rated-movies-count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().string("999")
                );
        verify(userService, times(1)).fetchMovieRateCount(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should count actors rated by user correctly")
    public void shouldCountActorsRatedByUserCorrectly() throws Exception {
        when(userService.fetchActorRateCount(anyLong())).thenReturn(999L);

        mockMvc.perform(get("/api/user/rated-actors-count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().string("999")
                );
        verify(userService, times(1)).fetchActorRateCount(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should count directors rated by user correctly")
    public void shouldCountDirectorsRatedByUserCorrectly() throws Exception {
        when(userService.fetchDirectorRateCount(anyLong())).thenReturn(999L);

        mockMvc.perform(get("/api/user/rated-directors-count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().string("999")
                );
        verify(userService, times(1)).fetchDirectorRateCount(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find all movies rated by user correctly")
    public void shouldFindAllMoviesRatedByUserCorrectly() throws Exception {
        when(userService.fetchAllRatedMovies(anyLong(), anyInt(), anyInt())).thenReturn(
                new PageImpl<>(List.of(MovieDtoWithUserRate.builder().id(1L).build()), pageable, 1)
        );

        mockMvc.perform(get("/api/user/rated-movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("pageNo", "0")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0]id").value(1)
                );
        verify(userService, times(1)).fetchAllRatedMovies(anyLong(), anyInt(), anyInt());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when find all movies rated by user and service throws ResourceNotFoundException")
    public void shouldReturnEmptyListWhenFindAllMoviesRatedByUserAndServiceThrowsResourceNotFoundException() throws Exception {
        when(userService.fetchAllRatedMovies(anyLong(), anyInt(), anyInt())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/user/rated-movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("pageNo", "0")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchAllRatedMovies(anyLong(), anyInt(), anyInt());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find all actors rated by user correctly")
    public void shouldFindAllActorsRatedByUserCorrectly() throws Exception {
        when(userService.fetchAllRatedActors(anyLong(), anyInt(), anyInt())).thenReturn(
                new PageImpl<>(List.of(ActorDtoWithUserRate.builder().id(1L).build()), pageable, 1)
        );

        mockMvc.perform(get("/api/user/rated-actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("pageNo", "1")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0]id").value(1)
                );
        verify(userService, times(1)).fetchAllRatedActors(anyLong(), anyInt(), anyInt());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when find all actors rated by user and no movies found")
    public void shouldReturnEmptyListWhenFindAllActorsRatedByUserAndNoActorsFound() throws Exception {
        when(userService.fetchAllRatedActors(anyLong(), anyInt(), anyInt())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/user/rated-actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("pageNo", "0")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchAllRatedActors(anyLong(), anyInt(), anyInt());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should find all directors rated by user correctly")
    public void shouldFindAllDirectorsRatedByUserCorrectly() throws Exception {
        when(userService.fetchAllRatedDirectors(anyLong(), anyInt(), anyInt())).thenReturn(
                new PageImpl<>(List.of(DirectorDtoWithUserRate.builder().id(1L).build()), pageable, 1)
        );

        mockMvc.perform(get("/api/user/rated-directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("pageNo", "1")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1),
                        jsonPath("$.[0]id").value(1)
                );
        verify(userService, times(1)).fetchAllRatedDirectors(anyLong(), anyInt(), anyInt());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return empty list when find all directors rated by user and no movies found")
    public void shouldReturnEmptyListWhenFindAllDirectorsRatedByUserAndNoDirectorsFound() throws Exception {
        when(userService.fetchAllRatedDirectors(anyLong(), anyInt(), anyInt())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/user/rated-directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", "1")
                        .param("pageNo", "1")
                        .param("pageSize", "1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(userService, times(1)).fetchAllRatedDirectors(anyLong(), anyInt(), anyInt());
    }
}

package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MovieServiceImpl movieService;
    @Autowired
    private ObjectMapper mapper;

    private MovieDto movieDto;
    private MovieDtoWithDetails movieDtoWithDetails;
    private MovieDtoBasic movieDtoBasic;
    private MovieUpdateDto movieUpdateDto;
    private RateDto rateDto;
    private final LocalDate date = LocalDate.of(2020, 1, 1);

    @BeforeEach
    void setUp() {
        movieDto = MovieDto.builder()
                .title("Movie")
                .description("Movie description")
                .category(MovieCategory.COMEDY)
                .directorId(1L)
                .releaseDate(date)
                .build();
        movieDtoWithDetails = MovieDtoWithDetails.builder()
                .id(1L)
                .title("title")
                .description("description")
                .rating(5.0)
                .releaseDate(date)
                .category("COMEDY")
                .build();
        movieDtoBasic = MovieDtoBasic.builder()
                .id(1L)
                .title("title")
                .category("COMEDY")
                .build();
        movieUpdateDto = MovieUpdateDto.builder()
                .title("title")
                .description("description")
                .releaseDate(LocalDate.now())
                .category(MovieCategory.COMEDY)
                .directorId(1L)
                .build();
        rateDto = RateDto.builder()
                .entityId(1L)
                .rate(1)
                .build();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN enter ADMIN endpoints")
    public void shouldAllowAdminEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", 1L)).andExpect(status().isAccepted());
        verify(movieService, times(1)).deleteMovie(anyLong());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow MODERATOR enter ADMIN endpoints")
    public void shouldAllowModeratorEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", 1L)).andExpect(status().isAccepted());
        verify(movieService, times(1)).deleteMovie(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny USER enter ADMIN endpoints")
    public void shouldDenyUserEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/movies")).andExpect(status().isForbidden());
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should allow ANONYMOUS enter GET endpoints")
    public void shouldAllowAnonymousEnterGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/movies")).andExpect(status().isOk());
        verify(movieService, times(1)).fetchAllMovies(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add movie correctly")
    public void shouldAddMovieCorrectly() throws Exception {
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/movies")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
        verify(movieService, times(1)).addMovie(any(MovieDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and title is null")
    public void shouldRespond400WhenAddActorAndTitleIsNull() throws Exception {
        movieDto.setTitle(null);
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Movie title cannot be empty"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and description exceed character limit")
    public void shouldRespond400WhenAddActorAndDescriptionExceedCharacterLimit() throws Exception {
        movieDto.setDescription("a".repeat(1001));
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Movie description cannot exceed 1000 characters"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and release date is in future")
    public void shouldRespond400WhenAddActorAndReleaseDateIsInFuture() throws Exception {
        movieDto.setReleaseDate(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Release date cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and movie category is null")
    public void shouldRespond400WhenAddActorAndMovieCategoryIsNull() throws Exception {
        movieDto.setCategory(null);
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Category is required"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 500 when missing request body")
    public void shouldRespond500WhenMissingRequestBody() throws Exception {
        movieDto.setCategory(null);
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isInternalServerError()
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete movie correctly")
    public void shouldDeleteMovieCorrectly() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(movieService, times(1)).deleteMovie(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when delete movie and service throws InvalidIdException")
    public void shouldRespond400WhenDeleteMovieAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(movieService).deleteMovie(anyLong());

        mockMvc.perform(delete("/api/movies/{id}", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(movieService, times(1)).deleteMovie(anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add actor to movie correctly")
    public void shouldAddActorToMovieCorrectly() throws Exception {
        when(movieService.addActorToMovie(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/movies/{movieId}/add-actor/{actorId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.statusCode").value("200"),
                    jsonPath("$.statusMessage").value("Ok")
                );
        verify(movieService, times(1)).addActorToMovie(anyLong(), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor to movie and actor is already added")
    public void shouldRespond400WhenAddActorToMovieAndActorIsAlreadyAdded() throws Exception {
        when(movieService.addActorToMovie(anyLong(), anyLong())).thenReturn(false);

        mockMvc.perform(post("/api/movies/{movieId}/add-actor/{actorId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("Actor already added to movie")
                );
        verify(movieService, times(1)).addActorToMovie(anyLong(), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor to movie and service throws InvalidIdException")
    public void shouldRespond400WhenAddActorToMovieAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(movieService).addActorToMovie(anyLong(), anyLong());

        mockMvc.perform(post("/api/movies/{movieId}/add-actor/{actorId}", -1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(movieService, times(1)).addActorToMovie(anyLong(), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should remove actor from movie correctly")
    public void shouldRemoveActorFromMovieCorrectly() throws Exception {
        when(movieService.removeActorFromMovie(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/movies/{movieId}/remove-actor/{actorId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(movieService, times(1)).removeActorFromMovie(anyLong(), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when remove actor from movie and actor is not added to movie")
    public void shouldRespond400WhenRemoveActorFromMovieAndActorIsNotAddedToMovie() throws Exception {
        when(movieService.removeActorFromMovie(anyLong(), anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/movies/{movieId}/remove-actor/{actorId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value("Actor is not added to this movie")
                );
        verify(movieService, times(1)).removeActorFromMovie(anyLong(), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when remove actor from movie and service throws InvalidIdException")
    public void shouldRespond400WhenRemoveActorFromMovieAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(movieService).removeActorFromMovie(anyLong(), anyLong());

        mockMvc.perform(delete("/api/movies/{movieId}/remove-actor/{actorId}", -1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(movieService, times(1)).removeActorFromMovie(anyLong(), anyLong());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should fetch movie by ID correctly")
    public void shouldFetchMovieByIdCCorrectly() throws Exception {
        when(movieService.fetchMovieDetailsById(anyLong())).thenReturn(movieDtoWithDetails);

        mockMvc.perform(get("/api/movies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("title"),
                        jsonPath("$.id").value("1")
                );
        verify(movieService, times(1)).fetchMovieDetailsById(anyLong());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when fetch movie by ID and service throws InvalidIdException")
    public void shouldRespond400WhenFetchMovieByIdAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(movieService).fetchMovieDetailsById(anyLong());

        mockMvc.perform(get("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(movieService, times(1)).fetchMovieDetailsById(anyLong());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 404 when fetch movie by ID and service throws ResourceNotFoundException")
    public void shouldRespond404WhenFetchMovieByIdAndServiceThrowsResourceNotFoundException() throws Exception {
        when(movieService.fetchMovieDetailsById(anyLong())).thenThrow(new ResourceNotFoundException("movie", "movie", "1"));

        mockMvc.perform(get("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(movieService, times(1)).fetchMovieDetailsById(anyLong());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should fetch all movies by title correctly")
    public void shouldFetchAllMoviesByTitleCorrectly() throws Exception {
        when(movieService.fetchAllMoviesByTitle(anyString(), anyString())).thenReturn(List.of(movieDtoBasic));

        mockMvc.perform(get("/api/movies/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "title")
                        .param("sorted", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("1"),
                        jsonPath("$.[0].title").value("title")
                );
        verify(movieService, times(1)).fetchAllMoviesByTitle(anyString(),anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should fetch all movies correctly with no sorted param")
    public void shouldFetchAllMoviesByTitleCorrectlyWithNoSortedParam() throws Exception {
        when(movieService.fetchAllMoviesByTitle(anyString(), anyString())).thenReturn(List.of(movieDtoBasic));

        mockMvc.perform(get("/api/movies/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "title"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("1"),
                        jsonPath("$.[0].title").value("title")
                );
        verify(movieService, times(1)).fetchAllMoviesByTitle(anyString(),anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should return empty list when fetch all movies by title and no movies found")
    public void shouldReturnEmptyListWhenFetchAllMoviesByTitleAndNoMoviesFound() throws Exception {
        when(movieService.fetchAllMoviesByTitle(anyString(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/movies/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "title"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("0"),
                        content().string("[]")
                );
        verify(movieService, times(1)).fetchAllMoviesByTitle(anyString(),anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should fetch all movies correctly")
    public void shouldFetchAllMoviesCorrectly() throws Exception {
        when(movieService.fetchAllMovies(anyString())).thenReturn(List.of(movieDtoBasic));

        mockMvc.perform(get("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sorted", "ASC"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("1"),
                        jsonPath("$.[0].title").value("title")
                );
        verify(movieService, times(1)).fetchAllMovies(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should fetch all movies correctly with no sorted param")
    public void shouldFetchAllMoviesCorrectlyWithNoSortedParam() throws Exception {
        when(movieService.fetchAllMovies(anyString())).thenReturn(List.of(movieDtoBasic));

        mockMvc.perform(get("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("1"),
                        jsonPath("$.[0].title").value("title")
                );
        verify(movieService, times(1)).fetchAllMovies(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should fetch all movies correctly with no sorted param")
    public void shouldReturnEmptyListWhenFetchAllMoviesAndNoMoviesFound() throws Exception {
        when(movieService.fetchAllMovies( anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value("0"),
                        content().string("[]")
                );
        verify(movieService, times(1)).fetchAllMovies(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update movie correctly")
    public void shouldUpdateMovieCorrectly() throws Exception {
        doNothing().when(movieService).updateMovie(anyLong(), any(MovieUpdateDto.class));

        mockMvc.perform(patch("/api/movies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movieUpdateDto)))
                .andExpectAll(
                  status().isAccepted(),
                  jsonPath("$.statusCode").value("202"),
                  jsonPath("$.statusMessage").value("Accepted")
                );
        verify(movieService, times(1)).updateMovie(anyLong(), any(MovieUpdateDto.class));

    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when update movie and title exceed character limit")
    public void shouldReturn400WhenUpdateMovieAndTitleExceedCharacterLimit() throws Exception {
        movieUpdateDto.setTitle("a".repeat(256));
        doNothing().when(movieService).updateMovie(anyLong(), any(MovieUpdateDto.class));

        mockMvc.perform(patch("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Title cannot exceed 255 characters"))
                );
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when update movie and title exceed character limit")
    public void shouldReturn400WhenUpdateMovieAndReleaseDateIsInFuture() throws Exception {
        movieUpdateDto.setReleaseDate(LocalDate.now().plusDays(1));

        mockMvc.perform(patch("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Release date cannot be in the future"))
                );
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when update movie and director ID is negative")
    public void shouldReturn400WhenUpdateMovieAndDirectorIdIsNegative() throws Exception {
        movieUpdateDto.setDirectorId(-1L);

        mockMvc.perform(patch("/api/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Director ID cannot less than 1"))
                );
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 when update movie and service throws InvalidIdException")
    public void shouldReturn400WhenUpdateMovieAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(movieService).updateMovie(anyLong(), any(MovieUpdateDto.class));

        mockMvc.perform(patch("/api/movies/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(movieService, times(1)).updateMovie(anyLong(), any(MovieUpdateDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when update movie and service throws ResourceNotFoundException")
    public void shouldReturn404WhenUpdateMovieAndServiceThrowsResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("movie", "movie", "1")).when(movieService).updateMovie(anyLong(), any(MovieUpdateDto.class));

        mockMvc.perform(patch("/api/movies/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(movieService, times(1)).updateMovie(anyLong(), any(MovieUpdateDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add rate to movie correctly with min rate")
    public void shouldAddRateToMovieCorrectlyWithMinRate() throws Exception {
        mockMvc.perform(post("/api/movies/rate/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(rateDto)))
                .andExpectAll(
                  status().isOk(),
                  jsonPath("$.statusCode").value("200"),
                  jsonPath("$.statusMessage").value("Ok")
                );
        verify(movieService, times(1)).addRateToMovie(any(RateDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add rate to movie correctly with max rate")
    public void shouldAddRateToMovieCorrectlyWithMaxRate() throws Exception {
        rateDto.setRate(6);

        mockMvc.perform(post("/api/movies/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rateDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.statusCode").value("200"),
                        jsonPath("$.statusMessage").value("Ok")
                );
        verify(movieService, times(1)).addRateToMovie(any(RateDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add rate to movie with rate higher than max rate")
    public void shouldRespond400WhenAddRateToMovieWithRateHigherThanMaxRate() throws Exception {
        rateDto.setRate(7);

        mockMvc.perform(post("/api/movies/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Rate must be between 1 and 6"))
                );
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add rate to movie with rate less than min rate")
    public void shouldRespond400WhenAddRateToMovieWithRateLessThanMinRate() throws Exception {
        rateDto.setRate(7);

        mockMvc.perform(post("/api/movies/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Rate must be between 1 and 6"))
                );
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add rate to movie with rate less than min rate")
    public void shouldRespond400WhenAddRateToMovieAndMovieIdIsLessThanOne() throws Exception {
        rateDto.setEntityId(-1L);

        mockMvc.perform(post("/api/movies/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Id cannot be less than 1"))
                );
        verifyNoInteractions(movieService);
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 404 when add rate to movie and service throws ResourceNotFoundException")
    public void shouldRespond400WhenAddRateToMovieAndServiceThrowsResourceNotFoundException() throws Exception {
        when(movieService.addRateToMovie(any(RateDto.class))).thenThrow(new ResourceNotFoundException("movie", "movie", "1"));
        mockMvc.perform(post("/api/movies/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rateDto)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString(("not found")))
                );
        verify(movieService, times(1)).addRateToMovie(any(RateDto.class));
    }
    @Test
    @WithMockUser
    @DisplayName("Should remove rate from movie correctly")
    public void shouldRemoveRateFromMovieCorrectly() throws Exception {
        mockMvc.perform(delete("/api/movies/rate/remove/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(movieService, times(1)).removeRate(anyLong());
    }
    @Test
    @WithMockUser
    @DisplayName("Should respond 400 when remove rate from movie and id is invalid")
    public void shouldRespond400WhenRemoveRateFromMovieAndIdIsInvalid() throws Exception {
        doThrow(new InvalidIdException()).when(movieService).removeRate(anyLong());

        mockMvc.perform(delete("/api/movies/rate/remove/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
        verify(movieService, times(1)).removeRate(anyLong());
    }

}
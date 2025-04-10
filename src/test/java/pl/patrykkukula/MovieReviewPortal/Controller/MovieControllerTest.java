package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.MovieServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private LocalDate date = LocalDate.of(2020,1,1);

    @BeforeEach
    void setUp(){
        movieDto = MovieDto.builder()
                .title("Movie")
                .description("Movie description")
                .category("COMEDY")
                .directorId(1L)
                .releaseDate(date)
                .build();
        movieDtoWithDetails = MovieDtoWithDetails.builder()
                .id(1L)
                .title("Movie with details")
                .description("Movie description")
                .rating(4.5)
                .releaseDate(date)
                .category("COMEDY")
                .director("Director")
                .actors(List.of("Actor"))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAddMovieCorrectly() throws Exception {
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/movies")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond403WhenAddMovieWithRoleUser() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isForbidden(),
                        content().string(containsString("Access Denied"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond400WhenRequestBodyIsInvalid() throws Exception {
        movieDto.setTitle("");
        movieDto.setReleaseDate(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Movie title cannot be null or empty")),
                        jsonPath("$.errorMessage").value(containsString("Release date cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteMovieCorrectly() throws Exception {
        mockMvc.perform(delete("/movies/{id}", 1L))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value(containsString("Accepted"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond400WhenInvalidId() throws Exception {

        doThrow(new InvalidIdException())
                .when(movieService).deleteMovie(-999L);

        mockMvc.perform(delete("/movies/{id}", -999L))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldAddActorToMovieCorrectly() throws Exception {
        when(movieService.addActorToMovie(anyLong(),anyLong())).thenReturn(true);

        mockMvc.perform(post("/movies/1/actors/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.statusCode").value("200"),
                        jsonPath("$.statusMessage").value("Ok")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond400WhenAddActorToMovieReturnsFalse() throws Exception {
        when(movieService.addActorToMovie(anyLong(),anyLong())).thenReturn(false);

        mockMvc.perform(post("/movies/1/actors/1"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("Actor already added to movie")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond404WhenActorDoesntExistWhileAddActorToMovie() throws Exception {
        doThrow(new ResourceNotFoundException("Actor", "actor id", "999"))
                .when(movieService).addActorToMovie(anyLong(),anyLong());

        mockMvc.perform(post("/movies/1/actors/999"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value("Resource Actor not found for field: actor id and value: 999")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRemoveActorFromMovieCorrectly() throws Exception {
        when(movieService.removeActorFromMovie(anyLong(),anyLong())).thenReturn(true);

        mockMvc.perform(delete("/movies/1/actors/1"))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldResponse404WhenRemoveActorFromMovieAndActorIsNotAddedToMovie() throws Exception {
        when(movieService.removeActorFromMovie(anyLong(),anyLong())).thenReturn(false);

        mockMvc.perform(delete("/movies/1/actors/1"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value("Actor is not added to this movie")
                );
    }
    @Test
    public void shouldFetchMovieByIdCorrectly() throws Exception {
        when(movieService.fetchMovieById(anyLong())).thenReturn(movieDtoWithDetails);

        mockMvc.perform(get("/movies/{id}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value("Movie with details"),
                        jsonPath("$.rating").value(4.5),
                        jsonPath("$.actors.length()").value(1),
                        jsonPath("$.actors[0]").value("Actor")
                );
    }
    @Test
    public void shouldRespond404WhenFetchMovieByIdAndMovieDoesntExist() throws Exception {
        doThrow(new ResourceNotFoundException("Movie", "movie id", "999"))
                .when(movieService).fetchMovieById(anyLong());

        mockMvc.perform(get("/movies/{id}", 999L))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value("Resource Movie not found for field: movie id and value: 999")
                );
    }
    @Test
    public void shouldFetchAllMoviesByTitleCorrectly() throws Exception {
        when(movieService.fetchAllMoviesByTitle(anyString(),anyString()))
                .thenReturn(List.of(MovieDtoBasic.builder().id(1L).title("Movie").build()));

        mockMvc.perform(get("/movies/search")
                .param("title", "Movie"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0].title").value("Movie")
                );
    }
    @Test
    public void shouldReturnEmptyListWhenFetchAllMoviesByTitleAndNoMoviesFound() throws Exception {
        when(movieService.fetchAllMoviesByTitle(anyString(),anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/movies/search")
                        .param("title", "Movie11"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }
    @Test
    public void shouldFetchAllMoviesCorrectly() throws Exception {
        when(movieService.fetchAllMovies(anyString()))
                .thenReturn(List.of(MovieDtoBasic.builder().id(1L).title("Movie").build()));

        mockMvc.perform(get("/movies"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0].title").value("Movie")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateMovieCorrectly() throws Exception {
        MovieUpdateDto dto = MovieUpdateDto.builder().title("Updated Movie").build();
        doNothing().when(movieService).updateMovie(anyLong(),any(MovieUpdateDto.class));

        mockMvc.perform(patch("/movies/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value(202),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond400WhenUpdateMovieWithInvalidRequestBody() throws Exception {
        MovieUpdateDto dto = MovieUpdateDto.builder().releaseDate(LocalDate.now().plusDays(1)).build();
        doNothing().when(movieService).updateMovie(anyLong(),any(MovieUpdateDto.class));

        mockMvc.perform(patch("/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value(containsString("Release date cannot be in the future"))
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldRespond401WhenUpdateMovieWithUserAnonymous() throws Exception {
        MovieUpdateDto dto = MovieUpdateDto.builder().title("Updated Movie").build();
        mockMvc.perform(patch("/movies/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().string(containsString("Unauthorized"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldAddRateToMovieCorrectly() throws Exception {
        MovieRateDto rate = MovieRateDto.builder()
                        .movieId(1L)
                        .rate(4)
                        .build();
        when(movieService.addRateToMovie(any(MovieRateDto.class))).thenReturn(1L);

        mockMvc.perform(post("/movies/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(rate)))
                .andExpectAll(
                  status().isOk(),
                  jsonPath("$.statusCode").value(200),
                  jsonPath("$.statusMessage").value("Ok")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond400WhenAddRateToMovieWithInvalidRequestBody() throws Exception {
        MovieRateDto rate = MovieRateDto.builder()
                .movieId(1L)
                .rate(999)
                .build();

        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rate)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value("rate: Rate must be between 1 and 6")
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldRespond401WhenAddRateToMovieWithUserAnonymous() throws Exception {
        MovieRateDto rate = MovieRateDto.builder()
                .movieId(1L)
                .rate(999)
                .build();

        mockMvc.perform(post("/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rate)))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().string(containsString("401")),
                        content().string(containsString("Unauthorized"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRemoveRateFromMovieCorrectly() throws Exception {
        when(movieService.removeRate(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/movies/{movieId}/rate", 1L))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value(202),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond404WhenRemoveRateFromMovieAndUserDidntSetRate() throws Exception {
        when(movieService.removeRate(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/movies/{movieId}/rate", 1L))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.errorMessage").value("You didn't set rate for this movie")
                );
    }
}

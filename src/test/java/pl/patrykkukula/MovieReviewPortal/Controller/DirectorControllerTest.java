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
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.of;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class DirectorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DirectorServiceImpl directorService;
    @Autowired
    private ObjectMapper mapper;

    private final LocalDate date = of(2020, 1, 1);
    private DirectorDto directorDto;

    @BeforeEach
    void setUp() {
        directorDto = DirectorDto.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .build();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow ADMIN enter ADMIN endpoints")
    public void shouldAllowAdminEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/directors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        verify(directorService,times(1)).removeDirector(anyLong());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow MODERATOR enter ADMIN endpoints")
    public void shouldAllowModeratorEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/directors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        verify(directorService,times(1)).removeDirector(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny USER enter ADMIN endpoints")
    public void shouldDenyUserEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/directors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verifyNoInteractions(directorService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should deny ANONYMOUS enter ADMIN endpoints")
    public void shouldDenyAnonymousEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/directors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        verifyNoInteractions(directorService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should allow ANONYMOUS enter ADMIN endpoints")
    public void shouldAllowAnonymousEnterAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/directors")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(directorService, times(1)).fetchAllDirectors(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add director correctly")
    public void shouldAddDirectorCorrectly() throws Exception {
        when(directorService.addDirector(any(DirectorDto.class))).thenReturn(1L);

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/directors/")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
        verify(directorService, times(1)).addDirector(any(DirectorDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add director and date of birth is invalid")
    public void shouldRespond400WhenAddDirectorAndDateOfBirthIsInvalid() throws Exception {
        LocalDate invalidDate = LocalDate.now().plusDays(1);
        directorDto.setDateOfBirth(invalidDate);

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add director and first name is null")
    public void shouldRespond400WhenAddDirectorAndFirstNameIsNull() throws Exception {
        directorDto.setFirstName(null);

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("First name cannot be null or empty"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add director and biography exceed character limit")
    public void shouldRespond400WhenAddDirectorAndBiographyExceedCharacterLimit() throws Exception {
        directorDto.setBiography("a".repeat(10000));

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("Biography must not exceed 1000 characters"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete director correctly")
    public void shouldDeleteDirectorCorrectly() throws Exception {
        doNothing().when(directorService).removeDirector(anyLong());

        mockMvc.perform(delete("/api/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when delete director and service throws InvalidIdException")
    public void shouldRespond400WhenDeleteDirectorAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(directorService).removeDirector(anyLong());

        mockMvc.perform(delete("/api/directors/{id}", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get director by id with movies correctly")
    public void shouldGetDirectorByIdWithMoviesCorrectly() throws Exception {
        DirectorDtoWithMovies directorDtoWithMovies = DirectorDtoWithMovies.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("England")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        when(directorService.fetchDirectorByIdWithMovies(anyLong())).thenReturn(directorDtoWithMovies);

        mockMvc.perform(get("/api/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDtoWithMovies)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.firstName").value("Alex"),
                        jsonPath("$.movies.size()").value(0)
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when get director by id with movies and service throws InvalidIdException")
    public void shouldRespond400WhenGetDirectorByIdWithMoviesAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(directorService).fetchDirectorByIdWithMovies(anyLong());

        mockMvc.perform(get("/api/directors/{id}", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get all directors correctly by name with invalid sort by param")
    public void shouldGetAllDirectorsCorrectlyByNameWithInvalidSortParam() throws Exception {
        when(directorService.fetchAllDirectors("ASC")).thenReturn(List.of(directorDto));

        mockMvc.perform(get("/api/directors").
                        param("sorted", "INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk()
                );
        verify(directorService, times(1)).fetchAllDirectors(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get all directors correctly by name when query params are empty")
    public void shouldGetAllDirectorsCorrectlyByNameWhenQueryParamsAreEmpty() throws Exception {
        when(directorService.fetchAllDirectors("ASC")).thenReturn(List.of(directorDto));

        mockMvc.perform(get("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(directorService, times(1)).fetchAllDirectors(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get all directors correctly with find by parameter")
    public void shouldGetAllDirectorsCorrectlyWithFindByParameter() throws Exception {
        when(directorService.fetchAllDirectorsByNameOrLastName(anyString(), anyString())).thenReturn(List.of(directorDto));

        mockMvc.perform(get("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("findBy", "John"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(directorService, times(1)).fetchAllDirectorsByNameOrLastName(anyString(), anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get top rated directors correctly")
    public void shouldGetTopRatedDirectorsCorrectly() throws Exception {
        when(directorService.fetchTopRatedDirectors()).thenReturn(List.of(new DirectorDtoWithUserRate()));

        mockMvc.perform(get("/api/directors/top-rated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(directorService, times(1)).fetchTopRatedDirectors();
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should return empty body when get top rated directors and no directors found")
    public void shouldReturnEmptyBodyWhenGetTopRatedDirectorsAndNoDirectorsFound() throws Exception {
        when(directorService.fetchTopRatedDirectors()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/directors/top-rated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(directorService, times(1)).fetchTopRatedDirectors();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update director correctly with all parameters")
    public void shouldUpdateDirectorCorrectlyWithAllParameters() throws Exception {
        DirectorUpdateDto directorUpdateDto = DirectorUpdateDto.builder()
                .firstName("Mark")
                .lastName("Smith")
                .country("Poland")
                .dateOfBirth(LocalDate.now())
                .biography("Biography")
                .build();
        doNothing().when(directorService).updateDirector(directorUpdateDto, 1L);

        mockMvc.perform(patch("/api/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorUpdateDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(directorService, times(1)).updateDirector(any(DirectorUpdateDto.class), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when update director with invalid date of birth")
    public void shouldRespond400WhenUpdateDirectorWithInvalidDateOfBirth() throws Exception {
        DirectorUpdateDto directorUpdateDto = DirectorUpdateDto.builder().dateOfBirth(LocalDate.now().plusDays(1)).build();
        mockMvc.perform(patch("/api/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when update director and biography exceed character limit")
    public void shouldRespond400WhenUpdateDirectorAndBiographyExceedCharacterLimit() throws Exception {
        DirectorUpdateDto directorUpdateDto = DirectorUpdateDto.builder().biography("a".repeat(1001)).build();
        mockMvc.perform(patch("/api/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Biography must not exceed 1000 characters"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when update director and service throws InvalidIdException")
    public void shouldRespond400WhenUpdateDirectorAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(directorService).updateDirector(any(DirectorUpdateDto.class), anyLong());

        mockMvc.perform(patch("/api/directors/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new DirectorUpdateDto())))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should add rate to director correctly with max rate")
    public void shouldAddRateToDirectorCorrectlyWithMaxRate() throws Exception {
        when(directorService.addRateToDirector(any(RateDto.class))).thenReturn(new RatingResult(6.0, true));

        mockMvc.perform(post("/api/directors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(5, 1L))))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.avgRate").value("6.0"),
                        jsonPath("$.rate").value("6.0")
                );
        verify(directorService, times(1)).addRateToDirector(any(RateDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should add rate to director correctly with min rate")
    public void shouldAddRateToDirectorCorrectlyWithMinRate() throws Exception {
        when(directorService.addRateToDirector(any(RateDto.class))).thenReturn(new RatingResult(1.0, true));

        mockMvc.perform(post("/api/directors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(1, 1L))))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.avgRate").value("1.0"),
                        jsonPath("$.rate").value("1.0")
                );
        verify(directorService, times(1)).addRateToDirector(any(RateDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when add rate to director with rate less then min")
    public void shouldRespond400WhenAddRateToDirectorWithRateLessThenMin() throws Exception {
        mockMvc.perform(post("/api/directors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(-1, 1L))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Rate must be between 1 and 6"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when add rate to director with rate higher then max")
    public void shouldRespond400WhenAddRateToDirectorWithRateHigherThenMax() throws Exception {
        mockMvc.perform(post("/api/directors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(7, 1L))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Rate must be between 1 and 6"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when add rate to director with negative director id")
    public void shouldRespond400WhenAddRateToDirectorWithNegativeDirectorId() throws Exception {
        mockMvc.perform(post("/api/directors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(6, -1L))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Id cannot be less than 1"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should remove rate from director correctly")
    public void shouldRemoveRateFromDirectorCorrectly() throws Exception {
        when(directorService.removeRate(anyLong())).thenReturn(3.0);

        mockMvc.perform(delete("/api/directors/rate/remove/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(directorService, times(1)).removeRate(anyLong());
    }
}
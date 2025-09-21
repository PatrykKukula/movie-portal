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
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.of;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class ActorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ActorServiceImpl actorService;
    @Autowired
    private ObjectMapper mapper;

    private final LocalDate date = of(2020, 1, 1);
    private ActorDto actorDto;

    @BeforeEach
    void setUp() {
        actorDto = ActorDto.builder()
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
        mockMvc.perform(delete("/api/actors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        verify(actorService,times(1)).removeActor(anyLong());
    }
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("Should allow MODERATOR enter ADMIN endpoints")
    public void shouldAllowModeratorEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/actors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        verify(actorService,times(1)).removeActor(anyLong());
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny USER enter ADMIN endpoints")
    public void shouldDenyUserEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/actors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
        verifyNoInteractions(actorService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should deny ANONYMOUS enter ADMIN endpoints")
    public void shouldDenyAnonymousEnterAdminEndpoints() throws Exception {
        mockMvc.perform(delete("/api/actors/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
        verifyNoInteractions(actorService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should allow ANONYMOUS enter GET endpoints")
    public void shouldAllowAnonymousEnterGetEndpoints() throws Exception {
        mockMvc.perform(get("/api/actors")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        verify(actorService,times(1)).fetchAllActors(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should add actor correctly")
    public void shouldAddActorCorrectly() throws Exception {
        when(actorService.addActor(any(ActorDto.class))).thenReturn(1L);

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/actors/")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
        verify(actorService, times(1)).addActor(any(ActorDto.class));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and date of birth is invalid")
    public void shouldRespond400WhenAddActorAndDateOfBirthIsInvalid() throws Exception {
        LocalDate invalidDate = LocalDate.now().plusDays(1);
        actorDto.setDateOfBirth(invalidDate);

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and first name is null")
    public void shouldRespond400WhenAddActorAndFirstNameIsNull() throws Exception {
        actorDto.setFirstName(null);

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("Name cannot be empty"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when add actor and biography exceed character limit")
    public void shouldRespond400WhenAddActorAndBiographyExceedCharacterLimit() throws Exception {
        actorDto.setBiography("a".repeat(10000));

        mockMvc.perform(post("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("Biography must not exceed 1000 characters"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete actor correctly")
    public void shouldDeleteActorCorrectly() throws Exception {
        doNothing().when(actorService).removeActor(anyLong());

        mockMvc.perform(delete("/api/actors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when delete actor and service throws InvalidIdException")
    public void shouldRespond400WhenDeleteActorAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(actorService).removeActor(anyLong());

        mockMvc.perform(delete("/api/actors/{id}", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get actor by id with movies correctly")
    public void shouldGetActorByIdWithMoviesCorrectly() throws Exception {
        ActorDtoWithMovies actorDtoWithMovies = ActorDtoWithMovies.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("England")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        when(actorService.fetchActorByIdWithMovies(anyLong())).thenReturn(actorDtoWithMovies);

        mockMvc.perform(get("/api/actors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorDtoWithMovies)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.firstName").value("Alex"),
                        jsonPath("$.movies.size()").value(0)
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when get actor by id with movies and service throws InvalidIdException")
    public void shouldRespond400WhenGetActorByIdWithMoviesAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(actorService).fetchActorByIdWithMovies(anyLong());

        mockMvc.perform(get("/api/actors/{id}", -1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get all actors correctly by name with invalid sort by param")
    public void shouldGetAllActorsCorrectlyByNameWithInvalidSortParam() throws Exception {
        when(actorService.fetchAllActors("ASC")).thenReturn(List.of(actorDto));

        mockMvc.perform(get("/api/actors").
                        param("sorted", "INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk()
                );
        verify(actorService, times(1)).fetchAllActors(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get all actors correctly by name when query params are empty")
    public void shouldGetAllActorsCorrectlyByNameWhenQueryParamsAreEmpty() throws Exception {
        when(actorService.fetchAllActors("ASC")).thenReturn(List.of(actorDto));

        mockMvc.perform(get("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(actorService, times(1)).fetchAllActors(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get all actors correctly with find by parameter")
    public void shouldGetAllActorsCorrectlyWithFindByParameter() throws Exception {
        when(actorService.fetchAllActorsByNameOrLastName(anyString(), anyString())).thenReturn(List.of(actorDto));

        mockMvc.perform(get("/api/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("findBy", "John"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(actorService, times(1)).fetchAllActorsByNameOrLastName(anyString(), anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should get top rated actors correctly")
    public void shouldGetTopRatedActorsCorrectly() throws Exception {
        when(actorService.fetchTopRatedActors()).thenReturn(List.of(new ActorDtoWithUserRate()));

        mockMvc.perform(get("/api/actors/top-rated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(1)
                );
        verify(actorService, times(1)).fetchTopRatedActors();
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should return empty body when get top rated actors and service throws ResourceNotFoundException")
    public void shouldReturnEmptyBodyWhenGetTopRatedActorsAndServiceThrowsResourceNotFoundException() throws Exception {
        when(actorService.fetchTopRatedActors()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/actors/top-rated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(0)
                );
        verify(actorService, times(1)).fetchTopRatedActors();
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update actor correctly with all parameters")
    public void shouldUpdateActorCorrectlyWithAllParameters() throws Exception {
        ActorUpdateDto actorUpdateDto = ActorUpdateDto.builder()
                .firstName("Mark")
                .lastName("Smith")
                .country("Poland")
                .dateOfBirth(LocalDate.now())
                .biography("Biography")
                .build();
        doNothing().when(actorService).updateActor(actorUpdateDto, 1L);

        mockMvc.perform(patch("/api/actors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorUpdateDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(actorService, times(1)).updateActor(any(ActorUpdateDto.class), anyLong());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when update actor with invalid date of birth")
    public void shouldRespond400WhenUpdateActorWithInvalidDateOfBirth() throws Exception {
        ActorUpdateDto actorUpdateDto = ActorUpdateDto.builder().dateOfBirth(LocalDate.now().plusDays(1)).build();
        mockMvc.perform(patch("/api/actors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when update actor and biography exceed character limit")
    public void shouldRespond400WhenUpdateActorAndBiographyExceedCharacterLimit() throws Exception {
        ActorUpdateDto actorUpdateDto = ActorUpdateDto.builder().biography("a".repeat(1001)).build();
        mockMvc.perform(patch("/api/actors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(actorUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Biography must not exceed 1000 characters"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should respond 400 when update actor and service throws InvalidIdException")
    public void shouldRespond400WhenUpdateActorAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(actorService).updateActor(any(ActorUpdateDto.class), anyLong());

        mockMvc.perform(patch("/api/actors/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ActorUpdateDto())))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should add rate to actor correctly with max rate")
    public void shouldAddRateToActorCorrectlyWithMaxRate() throws Exception {
        when(actorService.addRateToActor(any(RateDto.class))).thenReturn(new RatingResult(6.0, true));

        mockMvc.perform(post("/api/actors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(5, 1L))))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.avgRate").value("6.0"),
                        jsonPath("$.rate").value("6.0")
                );
        verify(actorService, times(1)).addRateToActor(any(RateDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should add rate to actor correctly with min rate")
    public void shouldAddRateToActorCorrectlyWithMinRate() throws Exception {
        when(actorService.addRateToActor(any(RateDto.class))).thenReturn(new RatingResult(1.0, true));

        mockMvc.perform(post("/api/actors/rate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RateDto(1, 1L))))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.avgRate").value("1.0"),
                        jsonPath("$.rate").value("1.0")
                );
        verify(actorService, times(1)).addRateToActor(any(RateDto.class));
    }
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should respond 400 when add rate to actor with rate less then min")
    public void shouldRespond400WhenAddRateToActorWithRateLessThenMin() throws Exception {
        mockMvc.perform(post("/api/actors/rate/add")
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
    @DisplayName("Should respond 400 when add rate to actor with rate higher then max")
    public void shouldRespond400WhenAddRateToActorWithRateHigherThenMax() throws Exception {
        mockMvc.perform(post("/api/actors/rate/add")
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
    @DisplayName("Should respond 400 when add rate to actor with negative actor id")
    public void shouldRespond400WhenAddRateToActorWithNegativeActorId() throws Exception {
        mockMvc.perform(post("/api/actors/rate/add")
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
    @DisplayName("Should remove rate from actor correctly")
    public void shouldRemoveRateFromActorCorrectly() throws Exception {
        when(actorService.removeRate(anyLong())).thenReturn(3.0);

        mockMvc.perform(delete("/api/actors/rate/remove/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
        verify(actorService, times(1)).removeRate(anyLong());
    }
}

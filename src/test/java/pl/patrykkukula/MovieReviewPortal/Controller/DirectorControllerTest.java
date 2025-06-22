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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;
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
    public void shouldAddDirectorCorrectly() throws Exception {
        when(directorService.addDirector(any(DirectorDto.class))).thenReturn(1L);

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", containsString("/directors/")),
                        jsonPath("$.statusCode").value("201"),
                        jsonPath("$.statusMessage").value("Created")
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond403WhenAddDirectorWithUserRole() throws Exception {

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isForbidden(),
                        content().string(containsString("403")),
                        content().string(containsString("Access Denied"))
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldRespond401WhenAddDirectorIfUserIsAnonymous() throws Exception {

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDto)))
                .andExpectAll(
                        status().isUnauthorized(),
                        content().string(containsString("401")),
                        content().string(containsString("Unauthorized"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond400WhenBodyRequestIsInvalidInAddDirector() throws Exception {
        DirectorDto invalidDirectorDto = new DirectorDto();
        LocalDate invalidDate = LocalDate.now().plusDays(1);
        invalidDirectorDto.setDateOfBirth(invalidDate);

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDirectorDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteDirectorCorrectly() throws Exception {
        doNothing().when(directorService).removeDirector(anyLong());

        mockMvc.perform(delete("/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithAnonymousUser
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

        mockMvc.perform(get("/directors/{id}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorDtoWithMovies)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.firstName").value("Alex"),
                        jsonPath("$.movies.size()").value(0)
                );
    }
    @Test
    @WithAnonymousUser
    public void shouldGetAllDirectorsCorrectlyByNameWithValidFindByParam() throws Exception {
        when(directorService.fetchAllDirectors("ASC")).thenReturn(List.of(directorDto));

        mockMvc.perform(get("/directors").
                        param("sorted","ASC")
                        .param("findBy","Hans")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk()
                );
        verify(directorService, times(1)).fetchAllDirectorsByNameOrLastName(anyString(), anyString());
    }
    @Test
    @WithAnonymousUser
    public void shouldGetAllDirectorsCorrectlyWhenFindByIsEmpty() throws Exception {
        when(directorService.fetchAllDirectors("ASC")).thenReturn(List.of(directorDto));

        mockMvc.perform(get("/directors").
                        param("sorted","ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk()
                );
        verify(directorService, times(1)).fetchAllDirectors(anyString());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateDirectorCorrectly() throws Exception {
        DirectorUpdateDto directorUpdateDto = DirectorUpdateDto.builder()
                .firstName("Mark")
                .build();
        doNothing().when(directorService).updateDirector(directorUpdateDto, 1L);

        mockMvc.perform(patch("/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorUpdateDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted")
                );
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldRespond400WhenRequestBodyIsInvalidInDirectorUpdate() throws Exception {
        DirectorUpdateDto directorUpdateDto = DirectorUpdateDto.builder()
                .dateOfBirth(LocalDate.now().plusDays(1))
                .build();
        doNothing().when(directorService).updateDirector(directorUpdateDto, 1L);

        mockMvc.perform(patch("/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string(containsString("Date of birth cannot be in the future"))
                );
    }
    @Test
    @WithMockUser(roles = "USER")
    public void shouldRespond403WhenUpdateDirectorWithUserRole() throws Exception {
        DirectorUpdateDto directorUpdateDto = DirectorUpdateDto.builder()
                .dateOfBirth(LocalDate.now().plusDays(1))
                .build();
        doNothing().when(directorService).updateDirector(directorUpdateDto, 1L);

        mockMvc.perform(patch("/directors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(directorUpdateDto)))
                .andExpectAll(
                        status().isForbidden(),
                        content().string(containsString("Access Denied"))
                );
    }
}

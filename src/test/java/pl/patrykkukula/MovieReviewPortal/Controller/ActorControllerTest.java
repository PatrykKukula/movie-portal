//package pl.patrykkukula.MovieReviewPortal.Controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithAnonymousUser;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
//import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
//import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;
//import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//
//import static java.time.LocalDate.of;
//import static org.hamcrest.Matchers.containsString;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ActiveProfiles(value = "test")
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ActorControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockitoBean
//    private ActorServiceImpl actorService;
//    @Autowired
//    private ObjectMapper mapper;
//
//    private final LocalDate date = of(2020, 1, 1);
//    private ActorDto actorDto;
//
//    @BeforeEach
//    void setUp() {
//        actorDto = ActorDto.builder()
//                .id(1L)
//                .firstName("Alex")
//                .lastName("Smith")
//                .country("Norway")
//                .dateOfBirth(date)
//                .build();
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void shouldAddActorCorrectly() throws Exception {
//        when(actorService.addActor(any(ActorDto.class))).thenReturn(1L);
//
//        mockMvc.perform(post("/actors")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(actorDto)))
//                .andExpectAll(
//                        status().isCreated(),
//                        header().string("Location", containsString("/actors/")),
//                        jsonPath("$.statusCode").value("201"),
//                        jsonPath("$.statusMessage").value("Created")
//                );
//    }
//    @Test
//    @WithMockUser(roles = "USER")
//    public void shouldRespond403WhenAddActorWithUserRole() throws Exception {
//        mockMvc.perform(post("/actors")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(actorDto)))
//                .andExpectAll(
//                        status().isForbidden(),
//                        content().string(containsString("403")),
//                        content().string(containsString("Access Denied"))
//                );
//    }
//    @Test
//    @WithAnonymousUser
//    public void shouldRespond401WhenAddActorIfUserIsAnonymous() throws Exception {
//        mockMvc.perform(post("/actors")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(actorDto)))
//                .andExpectAll(
//                        status().isUnauthorized(),
//                        content().string(containsString("401")),
//                        content().string(containsString("Unauthorized"))
//                );
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void shouldRespond400WhenBodyRequestIsInvalidInAddActor() throws Exception {
//        ActorDto invalidActorDto = new ActorDto();
//        LocalDate invalidDate = LocalDate.now().plusDays(1);
//        invalidActorDto.setDateOfBirth(invalidDate);
//
//        mockMvc.perform(post("/actors")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(invalidActorDto)))
//                .andExpectAll(
//                        status().isBadRequest(),
//                        jsonPath("$.statusCode").value("400"),
//                        jsonPath("$.statusMessage").value("Bad Request"),
//                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future"))
//                );
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void shouldDeleteActorCorrectly() throws Exception {
//            doNothing().when(actorService).removeActor(anyLong());
//
//            mockMvc.perform(delete("/actors/{id}", 1L)
//                    .contentType(MediaType.APPLICATION_JSON))
//                    .andExpectAll(
//                            status().isAccepted(),
//                            jsonPath("$.statusCode").value("202"),
//                            jsonPath("$.statusMessage").value("Accepted")
//                    );
//    }
//    @Test
//    @WithAnonymousUser
//    public void shouldGetActorByIdWithMoviesCorrectly() throws Exception {
//        ActorDtoWithMovies actorDtoWithMovies = ActorDtoWithMovies.builder()
//                .id(1L)
//                .firstName("Alex")
//                .lastName("Smith")
//                .country("England")
//                .dateOfBirth(date)
//                .movies(Collections.emptyList())
//                .build();
//        when(actorService.fetchActorByIdWithMovies(anyLong())).thenReturn(actorDtoWithMovies);
//
//        mockMvc.perform(get("/actors/{id}",1L)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(actorDtoWithMovies)))
//                .andExpectAll(
//                        status().isOk(),
//                        jsonPath("$.firstName").value("Alex"),
//                        jsonPath("$.movies.size()").value(0)
//                );
//        }
//    @Test
//    @WithAnonymousUser
//    public void shouldGetAllActorsCorrectlyByNameWithValidFindByParam() throws Exception {
//            when(actorService.fetchAllActors("ASC")).thenReturn(List.of(actorDto));
//
//            mockMvc.perform(get("/actors").
//                            param("sorted","ASC")
//                            .param("findBy","Hans")
//                            .contentType(MediaType.APPLICATION_JSON))
//                            .andExpectAll(
//                            status().isOk()
//                    );
//            verify(actorService, times(1)).fetchAllActorsByNameOrLastName(anyString(), anyString());
//    }
//    @Test
//    @WithAnonymousUser
//    public void shouldGetAllActorsCorrectlyWhenFindByIsEmpty() throws Exception {
//        when(actorService.fetchAllActors("ASC")).thenReturn(List.of(actorDto));
//
//        mockMvc.perform(get("/actors").
//                        param("sorted","ASC")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpectAll(
//                        status().isOk()
//                );
//        verify(actorService, times(1)).fetchAllActors(anyString());
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void shouldUpdateActorCorrectly() throws Exception {
//       ActorUpdateDto actorUpdateDto = ActorUpdateDto.builder()
//                .firstName("Mark")
//                .build();
//       doNothing().when(actorService).updateActor(actorUpdateDto, 1L);
//
//       mockMvc.perform(patch("/actors/{id}", 1L)
//               .contentType(MediaType.APPLICATION_JSON)
//               .content(mapper.writeValueAsString(actorUpdateDto)))
//               .andExpectAll(
//                       status().isAccepted(),
//                       jsonPath("$.statusCode").value("202"),
//                       jsonPath("$.statusMessage").value("Accepted")
//               );
//    }
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    public void shouldRespond400WhenRequestBodyIsInvalidInActorUpdate() throws Exception {
//        ActorUpdateDto actorUpdateDto = ActorUpdateDto.builder()
//                .dateOfBirth(LocalDate.now().plusDays(1))
//                .build();
//        doNothing().when(actorService).updateActor(actorUpdateDto, 1L);
//
//        mockMvc.perform(patch("/actors/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(actorUpdateDto)))
//                .andExpectAll(
//                        status().isBadRequest(),
//                        content().string(containsString("Date of birth cannot be in the past"))
//                );
//    }
//    @Test
//    @WithMockUser(roles = "USER")
//    public void shouldRespond403WhenUpdateActorWithUserRole() throws Exception {
//        ActorUpdateDto actorUpdateDto = ActorUpdateDto.builder()
//                .dateOfBirth(LocalDate.now().plusDays(1))
//                .build();
//        doNothing().when(actorService).updateActor(actorUpdateDto, 1L);
//
//        mockMvc.perform(patch("/actors/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(actorUpdateDto)))
//                .andExpectAll(
//                        status().isForbidden(),
//                        content().string(containsString("Access Denied"))
//                );
//    }
//
//}

package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ActorServiceImplTest {

    @Mock
    private ActorRepository actorRepository;
    @InjectMocks
    private ActorServiceImpl actorService;
    private ActorDto actorDto;
    private Actor actor1;
    private Actor actor2;
    private ActorUpdateDto actorUpdateDto;
    private final LocalDate date = of(2020, 1, 1);

   @BeforeEach
    void setUp() {
       actorDto = ActorDto.builder()
               .id(1L)
               .firstName("Alex")
               .lastName("Smith")
               .country("Norway")
               .dateOfBirth(date)
               .build();
       actor1 = Actor.builder()
               .actorId(1L)
               .firstName("Alex")
               .lastName("Smith")
               .country("Norway")
               .dateOfBirth(date)
               .movies(Collections.emptyList())
               .build();
       actor2 = Actor.builder()
               .actorId(1L)
               .firstName("Ben")
               .lastName("Milk")
               .country("USA")
               .dateOfBirth(date)
               .build();
       actorUpdateDto = ActorUpdateDto.builder()
               .firstName("Mark")
               .build();
   }
   @Test
   public void shouldAddActorCorrectly() {
       when(actorRepository.save(any(Actor.class))).thenReturn(actor1);

       Long actorId = actorService.addActor(actorDto);
       assertEquals(1L, actorId);
   }
   @Test
   public void shouldRemoveActorCorrectly() {
       when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor1));
       doNothing().when(actorRepository).deleteById(anyLong());

       actorService.removeActor(actorDto.getId());
       verify(actorRepository, times(1)).findById(anyLong());
       verify(actorRepository, times(1)).deleteById(anyLong());
   }
   @Test
   public void shouldThrowInvalidIdExceptionWhenIdIsInvalid() {
       InvalidIdException ex = assertThrows(InvalidIdException.class, () -> actorService.removeActor(-1L));
       assertTrue(ex.getMessage().contains("ID cannot be less than 1 or null"));
   }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenActorNotFound() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> actorService.removeActor(1L));
        assertTrue(ex.getMessage().contains("Resource Actor not found"));
   }
   @Test
   public void shouldFetchActorCorrectly() {
       when(actorRepository.findByIdWithMovies(anyLong())).thenReturn(Optional.of(actor1));

       ActorDtoWithMovies fetchedActor = actorService.fetchActorByIdWithMovies(1L);
       assertEquals(1L, fetchedActor.getId());
       assertEquals("Alex", fetchedActor.getFirstName());
       assertEquals(0, fetchedActor.getMovies().size());
   }

   @Test
   public void shouldFetchAllActorsCorrectlySortedAsc(){
       when(actorRepository.findAllSortedByNameAsc()).thenReturn(List.of(actor1, actor2));

       List<ActorDto> actors = actorService.fetchAllActors("ASC");
       assertEquals(2, actors.size());
       assertEquals("Alex", actors.get(0).getFirstName());
       assertEquals("Ben", actors.get(1).getFirstName());
   }
    @Test
    public void shouldFetchAllActorsCorrectlySortedDesc(){
        when(actorRepository.findAllSortedByNameDesc()).thenReturn(List.of(actor2, actor1));

        List<ActorDto> actors = actorService.fetchAllActors("DESC");
        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(1).getFirstName());
        assertEquals("Ben", actors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchAllActorsCorrectlySortedAscWhenInputSortIsInvalid(){
        when(actorRepository.findAllSortedByNameAsc()).thenReturn(List.of(actor1, actor2));

        List<ActorDto> actors = actorService.fetchAllActors("X");
        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
        assertEquals("Ben", actors.get(1).getFirstName());
    }
    @Test
    public void shouldReturnEmptyListWhenNoActorsFound() {
        when(actorRepository.findAllSortedByNameAsc()).thenReturn(Collections.emptyList());

        List<ActorDto> actors = actorService.fetchAllActors("ASC");
        assertEquals(0, actors.size());
    }
    @Test
    public void shouldFetchAllActorsCorrectlyByNameOrLastNameAsc(){
        when(actorRepository.findAllByFirstOrLastNameAsc(anyString())).thenReturn(List.of(actor1));

        List<ActorDto> actors = actorService.fetchAllActorsByNameOrLastName("Alex","ASC");
        assertEquals(1, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchReturnEmptyListWhenNoActorsFoundByNameOrLastNameAsc(){
        when(actorRepository.findAllByFirstOrLastNameAsc(anyString())).thenReturn(Collections.emptyList());

        List<ActorDto> actors = actorService.fetchAllActorsByNameOrLastName("d","ASC");
        assertEquals(0, actors.size());
    }
    @Test
    public void shouldUpdateActorCorrectly() {
       when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor1));
       when(actorRepository.save(any(Actor.class))).thenReturn(actor2);

       actorService.updateActor(actorUpdateDto, 1L);

       verify(actorRepository, times(1)).findById(anyLong());
       verify(actorRepository, times(1)).save(any(Actor.class));
    }
   }


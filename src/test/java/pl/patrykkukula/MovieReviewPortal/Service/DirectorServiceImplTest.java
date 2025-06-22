package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DirectorServiceImplTest {

    @Mock
    private DirectorRepository directorRepository;
    @InjectMocks
    private DirectorServiceImpl directorService;

    private DirectorDto directorDto;
    private Director director1;
    private Director director2;
    private DirectorUpdateDto directorUpdateDto;
    private final LocalDate date = of(2020, 1, 1);
    @BeforeEach
    void setUp() {
        directorDto = DirectorDto.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .build();
        director1 = Director.builder()
                .directorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        director2 = Director.builder()
                .directorId(1L)
                .firstName("Ben")
                .lastName("Milk")
                .country("USA")
                .dateOfBirth(date)
                .build();
        directorUpdateDto = DirectorUpdateDto.builder()
                .firstName("Mark")
                .build();
    }
    @Test
    public void shouldAddDirectorCorrectly() {
        when(directorRepository.save(any(Director.class))).thenReturn(director1);

        Long actorId = directorService.addDirector(directorDto);
        assertEquals(1L, actorId);
    }
    @Test
    public void shouldRemoveDirectorCorrectly() {
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director1));
        doNothing().when(directorRepository).deleteById(anyLong());

        directorService.removeDirector(directorDto.getId());
        verify(directorRepository, times(1)).findById(anyLong());
        verify(directorRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsInvalid() {
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> directorService.removeDirector(-1L));
        assertTrue(ex.getMessage().contains("ID cannot be less than 1 or null"));
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenDirectorNotFound() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> directorService.removeDirector(1L));
        assertTrue(ex.getMessage().contains("Resource Director not found"));
    }
    @Test
    public void shouldFetchDirectorCorrectly() {
        when(directorRepository.findByIdWithMovies(anyLong())).thenReturn(Optional.of(director1));

        DirectorDtoWithMovies fetchedActor = directorService.fetchDirectorByIdWithMovies(1L);
        assertEquals(1L, fetchedActor.getId());
        assertEquals("Alex", fetchedActor.getFirstName());
        assertEquals(0, fetchedActor.getMovies().size());
    }
    @Test
    public void shouldFetchAllDirectorsCorrectlySortedAsc(){
        when(directorRepository.findAllSortedAsc()).thenReturn(List.of(director1, director2));

        List<DirectorDto> directors = directorService.fetchAllDirectors("ASC");
        assertEquals(2, directors.size());
        assertEquals("Alex", directors.get(0).getFirstName());
        assertEquals("Ben", directors.get(1).getFirstName());
    }
    @Test
    public void shouldFetchAllDirectorsCorrectlySortedDesc(){
        when(directorRepository.findAllSortedDesc()).thenReturn(List.of(director2, director1));

        List<DirectorDto> directors = directorService.fetchAllDirectors("DESC");
        assertEquals(2, directors.size());
        assertEquals("Alex", directors.get(1).getFirstName());
        assertEquals("Ben", directors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchAllActorsCorrectlySortedAscWhenInputSortIsInvalid(){
        when(directorRepository.findAllSortedAsc()).thenReturn(List.of(director1, director2));

        List<DirectorDto> actors = directorService.fetchAllDirectors("X");
        assertEquals(2, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
        assertEquals("Ben", actors.get(1).getFirstName());
    }
    @Test
    public void shouldReturnEmptyListWhenNoDirectorsFound() {
        when(directorRepository.findAllSortedAsc()).thenReturn(Collections.emptyList());

        List<DirectorDto> actors = directorService.fetchAllDirectors("ASC");
        assertEquals(0, actors.size());
    }
    @Test
    public void shouldFetchAllDirectorsCorrectlyByNameOrLastNameAsc(){
        when(directorRepository.findAllByFirstOrLastNameSortedAsc(anyString())).thenReturn(List.of(director1));

        List<DirectorDto> actors = directorService.fetchAllDirectorsByNameOrLastName("Alex","ASC");
        assertEquals(1, actors.size());
        assertEquals("Alex", actors.get(0).getFirstName());
    }
    @Test
    public void shouldFetchReturnEmptyListWhenNoActorsFoundByNameOrLastNameAsc(){
        when(directorRepository.findAllByFirstOrLastNameSortedAsc(anyString())).thenReturn(Collections.emptyList());

        List<DirectorDto> actors = directorService.fetchAllDirectorsByNameOrLastName("d","ASC");
        assertEquals(0, actors.size());
    }
    @Test
    public void shouldUpdateActorCorrectly() {
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director1));
        when(directorRepository.save(any(Director.class))).thenReturn(director2);

        directorService.updateDirector(directorUpdateDto, 1L);

        verify(directorRepository, times(1)).findById(anyLong());
        verify(directorRepository, times(1)).save(any(Director.class));
    }
}

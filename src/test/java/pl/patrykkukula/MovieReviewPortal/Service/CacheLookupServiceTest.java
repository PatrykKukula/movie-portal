package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CacheLookupServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CacheLookupServiceTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private DirectorRepository directorRepository;
    @InjectMocks
    private CacheLookupServiceImpl cacheLookupService;
    private Movie movie;
    private Actor actor;
    private Director director;

    @BeforeEach
    public void setUp() {
        movie = Movie.builder()
                .movieId(1L)
                .title("Title")
                .build();
        actor = Actor.builder()
                .actorId(1L)
                .firstName("Alex")
                .build();
        director = Director.builder()
                .directorId(1L)
                .firstName("John")
                .build();
    }
        @Test
        public void shouldFindMovieByIdCorrectly() {
            when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));

            Movie movie = cacheLookupService.findMovieById(1L);

            assertEquals(1L, movie.getMovieId());
            assertEquals("Title", movie.getTitle());
        }
        @Test
        public void shouldThrowInvalidIdExceptionWhenFindMovieByIdAndInvalidId(){
            InvalidIdException ex = assertThrows(InvalidIdException.class, () -> cacheLookupService.findMovieById(-1L));
            assertEquals("ID cannot be less than 1 or null", ex.getMessage());
        }
        @Test
        public void shouldThrowResourceNotFoundExceptionWhenFindMovieByIdAndNoMovieFound(){
            when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> cacheLookupService.findMovieById(1L));
            assertTrue(ex.getMessage().contains("not found"));
        }
    @Test
    public void shouldFindActorByIdCorrectly() {
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor));

        Actor actor = cacheLookupService.findActorById(1L);

        assertEquals(1L, actor.getActorId());
        assertEquals("Alex", actor.getFirstName());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFindActorByIdAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> cacheLookupService.findActorById(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenFindActorByIdAndNoActorFound(){
        when(actorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> cacheLookupService.findActorById(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldFindDirectorByIdCorrectly() {
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));

        Director director = cacheLookupService.findDirectorById(1L);

        assertEquals(1L, director.getDirectorId());
        assertEquals("John", director.getFirstName());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFindDirectoryIdAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> cacheLookupService.findDirectorById(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenFindDirectorByIdAndNoDirectorFound(){
        when(directorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> cacheLookupService.findDirectorById(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }

    }


package pl.patrykkukula.MovieReviewPortal.Service;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRate.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.*;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    DirectorRepository directorRepository;
    @Mock
    ActorRepository actorRepository;
    @Mock
    UserEntityRepository userEntityRepository;
    @Mock
    MovieRateRepository movieRateRepository;
    @Mock
    private Tuple tuple;
    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1;
    private Movie movie2;
    private LocalDate date = LocalDate.of(2000,12,12);
    private MovieDto movieDto;
    private Director director;
    private Actor actor;
    private MovieRate movieRate;
    private MovieRateDto movieRateDto;
    private List<Actor> actors = new ArrayList<>();


    @BeforeEach
    void setUp() {
        movieDto = MovieDto.builder()
                .id(1L)
                .title("Movie")
                .description("Description")
                .releaseDate(date)
                .category(MovieCategory.ACTION)
//                .directorId(1L)
                .build();
        director = Director.builder()
                .directorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        actor = Actor.builder()
                .actorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .country("Norway")
                .dateOfBirth(date)
                .movies(Collections.emptyList())
                .build();
        movie1 = Movie.builder()
                .movieId(1L)
                .title("Movie")
                .description("Description")
                .releaseDate(date)
                .category(MovieCategory.ACTION)
                .actors(actors)
                .director(director)
                .build();
        movie2 = Movie.builder()
                .movieId(2L)
                .title("Another Movie")
                .description("Another Description")
                .releaseDate(date)
                .category(MovieCategory.ACTION)
                .actors(actors)
                .director(director)
                .build();
        movieRate = MovieRate.builder()
                .movieRateId(1L)
                .movie(movie1)
                .user(setCurrentUser())
                .rate(4)
                .build();
        movieRateDto = MovieRateDto.builder()
                .rate(4)
                .movieId(1L)
                .user(setCurrentUser())
                .build();
    }
    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

//    @Test
//    public void shouldAddMovieCorrectly(){
//        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));
//        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);
//
//        Long movieId = movieService.addMovie(movieDto);
//
//        assertEquals(1L, movieId);
//    }
//    @Test
//    public void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist(){
//        movieDto.setCategory("INVALID");
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.addMovie(movieDto));
//        assertTrue(ex.getMessage().contains("Category not found"));
//    }
//    @Test
//    public void shouldThrowResourceNotFoundExceptionWhenDirectorDoesNotExist(){
//        movieDto.setDirectorId(15L);
//
//        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.addMovie(movieDto));
//        assertTrue(ex.getMessage().contains("Director not found"));
//    }
    @Test
    public void shouldDeleteMovieCorrectly(){
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie1));
        doNothing().when(movieRepository).deleteById(anyLong());

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).findById(anyLong());
        verify(movieRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenIdIsLessThanOne(){

        assertThrows(InvalidIdException.class, () -> movieService.deleteMovie(-1L));
    }
    @Test
    public void shouldAddActorToMovieCorrectly(){
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor));
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isAdded = movieService.addActorToMovie(1L, 1L);
        assertTrue(isAdded);
    }
    @Test
    public void shouldReturnFalseWhenActorIsAlreadyAddedToMovie(){
        movie1.getActors().add(actor);
        when(actorRepository.findById(anyLong())).thenReturn(Optional.of(actor));
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isAdded = movieService.addActorToMovie(1L, 1L);
        assertFalse(isAdded);
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenActorDoesNotExist(){
        when(actorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.addActorToMovie(1L, 999L));
        assertTrue(ex.getMessage().contains("Actor not found"));
    }
    @Test
    public void shouldRemoveActorFromMovieCorrectly(){
        movie1.getActors().add(actor);
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isRemoved = movieService.removeActorFromMovie(1L, 1L);

        assertTrue(isRemoved);
    }
    @Test
    public void shouldReturnFalseWhenActorIsNotAddedToMovie(){
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isRemoved = movieService.removeActorFromMovie(1L, 1L);

        assertFalse(isRemoved);
    }
//    @Test
//    public void shouldFetchMovieByIdCorrectly(){
//        when(movieRepository.findByIdWithActorsAndMovieRates(anyLong())).thenReturn(Optional.of(tuple));
//        when(tuple.get("movie", Movie.class)).thenReturn(movie1);
//        when(tuple.get("rating", Double.class)).thenReturn(4.5);
//
//        MovieDtoWithDetails movieDtoWithDetails = movieService.fetchMovieDetailsById(1L);
//
//        assertEquals("Movie", movieDtoWithDetails.getTitle());
//        assertEquals(4.5, movieDtoWithDetails.getRating(), 0);
//    }
    @Test
    public void shouldFetchAllMoviesByTitleCorrectlyAsc(){
        when(movieRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(anyString())).thenReturn(List.of(movie2, movie1));

        List<MovieDtoBasic> movies = movieService.fetchAllMoviesByTitle("Movie", "ASC");

        assertEquals(2, movies.size());
        assertEquals("Another Movie",movies.get(0).getTitle());
        assertEquals("Movie", movies.get(1).getTitle());
    }
    @Test
    public void shouldFetchAllMoviesByTitleCorrectlyDesc(){
        when(movieRepository.findByTitleContainingIgnoreCaseOrderByTitleDesc(anyString())).thenReturn(List.of(movie1, movie2));

        List<MovieDtoBasic> movies = movieService.fetchAllMoviesByTitle("Movie", "DESC");

        assertEquals(2, movies.size());
        assertEquals("Another Movie",movies.get(1).getTitle());
        assertEquals("Movie", movies.get(0).getTitle());
    }
    @Test
    public void shouldReturnEmptyListWhenNoMoviesByTitleFound(){
        when(movieRepository.findByTitleContainingIgnoreCaseOrderByTitleDesc(anyString())).thenReturn(Collections.emptyList());

        List<MovieDtoBasic> movies = movieService.fetchAllMoviesByTitle("X", "DESC");

        assertEquals(0, movies.size());
    }
    @Test
    public void shouldFetchAllMoviesCorrectlyAsc(){
        when(movieRepository.findAllOrderByTitleAsc()).thenReturn(List.of(movie2, movie1));

        List<MovieDtoBasic> movies = movieService.fetchAllMovies("ASC");

        assertEquals(2, movies.size());
        assertEquals("Another Movie",movies.get(0).getTitle());
        assertEquals("Movie", movies.get(1).getTitle());
    }
    @Test
    public void shouldFetchAllMoviesCorrectlyDesc(){
        when(movieRepository.findAllOrderByTitleDesc()).thenReturn(List.of(movie1, movie2));

        List<MovieDtoBasic> movies = movieService.fetchAllMovies("DESC");

        assertEquals(2, movies.size());
        assertEquals("Another Movie",movies.get(1).getTitle());
        assertEquals("Movie", movies.get(0).getTitle());
    }
    @Test
    public void shouldFetchAllMoviesCorrectlySortedAscWithInvalidSortingInput(){
        when(movieRepository.findAllOrderByTitleAsc()).thenReturn(List.of(movie2, movie1));

        List<MovieDtoBasic> movies = movieService.fetchAllMovies("X");

        assertEquals(2, movies.size());
        assertEquals("Another Movie",movies.get(0).getTitle());
        assertEquals("Movie", movies.get(1).getTitle());
    }
    @Test
    public void shouldUpdateMovieCorrectly(){
        MovieUpdateDto movieUpdateDto = MovieUpdateDto.builder()
                        .title("Updated Title")
                        .directorId(1L)
                        .build();
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie1));
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);

        movieService.updateMovie(1L, movieUpdateDto);
        verify(movieRepository).save(captor.capture());
        Movie updatedMovie = captor.getValue();

        verify(movieRepository, times(1)).save(any(Movie.class));
        assertEquals("Updated Title", updatedMovie.getTitle());
    }
    @Test
    public void shouldAddRateToMovieCorrectly(){
        setAuthentication();
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(movieRepository.findByIdWithMovieRates(anyLong())).thenReturn(Optional.of(movie1));
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(),anyLong())).thenReturn(Optional.empty());
        when(movieRateRepository.save(any(MovieRate.class))).thenReturn(movieRate);

        Long addedRateId = movieService.addRateToMovie(movieRateDto);

        assertEquals(1L, addedRateId);
    }
    @Test
    public void shouldReturnCorrectIdWhileUpdatingMovieRate(){
        setAuthentication();
        movieRate.setMovieRateId(2L);
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(movieRepository.findByIdWithMovieRates(anyLong())).thenReturn(Optional.of(movie1));
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(),anyLong())).thenReturn(Optional.of(movieRate));
        when(movieRateRepository.save(any(MovieRate.class))).thenReturn(movieRate);

        Long updatedRate = movieService.addRateToMovie(movieRateDto);

        assertEquals(2L, updatedRate);
    }
    @Test
    public void shouldRemoveRateFromMovieCorrectly(){
        setAuthentication();
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(movieRateRepository.deleteByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(1);

        boolean isRemoved = movieService.removeRate(1L);

        assertTrue(isRemoved);
    }
    @Test
    public void shouldReturnFalseWhenRemoveRateWhileDidntSetRateToMovie(){
        setAuthentication();
        when(userEntityRepository.findByUsername(anyString())).thenReturn(Optional.of(setCurrentUser()));
        when(movieRateRepository.deleteByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(0);

        boolean isRemoved = movieService.removeRate(1L);

        assertFalse(isRemoved);
    }
    private UserEntity setCurrentUser() {
        Role role = Role.builder()
                .roleId(1L)
                .roleName("USER")
                .build();

        return UserEntity.builder()
                .userId(1L)
                .email("user@user.com")
                .password("password")
                .roles(List.of(role))
                .isEnabled(true)
                .build();
    }
    private void setAuthentication(){
        User user = new User("user@user.com","password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}

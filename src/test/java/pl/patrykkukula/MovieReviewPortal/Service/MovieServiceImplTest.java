package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRateRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CacheLookupServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    DirectorRepository directorRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    MovieRateRepository movieRateRepository;
    @Mock
    private CacheLookupServiceImpl cacheLookupService;
    @Mock
    private ActorRepository actorRepository;
    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie1;
    private Movie movie2;
    private final LocalDate date = LocalDate.of(2000,12,12);
    private MovieDto movieDto;
    private Director director;
    private Actor actor;
    private MovieRate movieRate;
    private MovieRate newRate;
    private RateDto rateDto;
    private MovieUpdateDto movieUpdateDto;
    private RatingResult ratingResult;
    private UserEntity user;
    private List<Actor> actors = new ArrayList<>();

    @BeforeEach
    void setUp() {
        movieDto = MovieDto.builder()
                .id(1L)
                .title("Movie")
                .description("Description")
                .releaseDate(date)
                .category(MovieCategory.ACTION)
                .directorId(1L)
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
        newRate = MovieRate.builder()
                .movieRateId(1L)
                .movie(movie1)
                .user(setCurrentUser())
                .rate(1)
                .build();
        rateDto = RateDto.builder()
                .rate(4)
                .entityId(1L)
                .build();
        movieUpdateDto = MovieUpdateDto.builder()
                .title("Updated Title")
                .directorId(1L)
                .category(MovieCategory.COMEDY)
                .build();
        ratingResult = new RatingResult(5.0, false);
        user = setCurrentUser();
    }
    @Test
    public void shouldAddMovieCorrectly(){
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie1);

        Long movieId = movieService.addMovie(movieDto);

        assertEquals(1L, movieId);
    }
    @Test
    public void shouldThrowInputMismatchExceptionWhenAddMovieAndSetInvalidCategory(){
        movieDto.setCategory(MovieCategory.ALL);

        InputMismatchException ex = assertThrows(InputMismatchException.class, () -> movieService.addMovie(movieDto));
        assertTrue(ex.getMessage().contains("Please choose valid category"));
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenDirectorDoesNotExist(){
        movieDto.setDirectorId(15L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.addMovie(movieDto));
        assertTrue(ex.getMessage().contains("Director not found"));
    }
    @Test
    public void shouldDeleteMovieCorrectly(){
        when(cacheLookupService.findMovieById(anyLong())).thenReturn(movie1);

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).deleteById(anyLong());
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenDeleteMovieAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> movieService.deleteMovie(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldAddRateToMovieCorrectlyWhenUserAlreadyRated(){
        movie1.setMovieRates(List.of(movieRate));
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(movieRate));
        when(movieRateRepository.save(any(MovieRate.class))).thenReturn(movieRate);

        RatingResult returnedResult = movieService.addRateToMovie(RateDto.builder().rate(1).entityId(1L).build());

        assertEquals(1, returnedResult.getRate());
    }
    @Test
    public void shouldAddRateToMovieCorrectlyWhenUserDidNotRateAlready(){
        movie1.setMovieRates(new ArrayList<>());
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(movieRepository.findByIdWithMovieRates(anyLong())).thenReturn(Optional.of(movie1));
        when(movieRateRepository.save(any(MovieRate.class))).thenReturn(newRate);

        RatingResult returnedResult = movieService.addRateToMovie(rateDto);

        assertEquals(1, returnedResult.getRate());
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenAddRateAndNotLoggedIn(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> movieService.addRateToMovie(rateDto));
        assertEquals("Log in to add rate", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenAddRateToMovieAndMovieNotFound(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(movieRepository.findByIdWithMovieRates(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.addRateToMovie(rateDto));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldRemoveRateFromMovieCorrectly(){
        movie1.setMovieRates(List.of(movieRate));
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRateRepository.deleteByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(1);
        when(movieRepository.findByIdWithMovieRates(anyLong())).thenReturn(Optional.of(movie1));

        Double averageRate = movieService.removeRate(1L);

        assertEquals(4, averageRate, 0.01);
    }
    @Test
    public void shouldThrowAccessDeniedExceptionWhenRemoveRateAndUserNotLoggedIn(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> movieService.removeRate(1L));
        assertEquals("Log in to remove rate", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenRemoveRateAndRateIsNotRemoved(){
        when(userDetailsService.getLoggedUserEntity()).thenReturn(Optional.of(user));
        when(movieRateRepository.deleteByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(0);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> movieService.removeRate(1L));
        assertEquals("Didn't remove rate successfully. Please try again", ex.getMessage());
    }
    @Test
    public void shouldAddActorToMovieCorrectly(){
        when(cacheLookupService.findActorById(anyLong())).thenReturn(actor);
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isAdded = movieService.addActorToMovie(1L, 1L);
        assertTrue(isAdded);
    }
    @Test
    public void shouldReturnFalseWhenAddActorToMovieAndActorIsAlreadyAdded(){
        movie1.getActors().add(actor);
        when(cacheLookupService.findActorById(anyLong())).thenReturn(actor);
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isAdded = movieService.addActorToMovie(1L, 1L);
        assertFalse(isAdded);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenAddActorToMovieWithInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> movieService.addActorToMovie(1L, -1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenAddActorToMovieAndActorDoesNotExist(){
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.addActorToMovie(1L, 999L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldRemoveActorFromMovieCorrectly(){
        movie1.getActors().add(actor);
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isRemoved = movieService.removeActorFromMovie(1L, 1L);

        assertTrue(isRemoved);
    }
    @Test
    public void shouldReturnFalseWhenRemoveActorAndActorIsNotAddedToMovie(){
        when(movieRepository.findByIdWithActors(anyLong())).thenReturn(Optional.of(movie1));

        boolean isRemoved = movieService.removeActorFromMovie(1L, 1L);

        assertFalse(isRemoved);
    }
    @Test
    public void shouldFetchMovieByIdCorrectly(){
        when(movieRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.of(movie1));
        when(movieRateRepository.getAverageMovieRate(anyLong())).thenReturn(5.0);
        when(movieRepository.countMovieRates(anyLong())).thenReturn(100);

        MovieDtoWithDetails movieDtoWithDetails = movieService.fetchMovieDetailsById(1L);

        assertEquals("Movie", movieDtoWithDetails.getTitle());
        assertEquals(5, movieDtoWithDetails.getRating(), 0);
    }
    @Test
    public void shouldThrowResourceNorFoundExceptionWhenFetchMovieByIdAneMovieDoesNotExist(){
        when(movieRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.fetchMovieDetailsById(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldReturnAverageRateZeroWhenNoRatesAddedToMovie(){
        when(movieRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.of(movie1));
        when(movieRateRepository.getAverageMovieRate(anyLong())).thenReturn(0.0);
        when(movieRepository.countMovieRates(anyLong())).thenReturn(0);

        MovieDtoWithDetails movieDtoWithDetails = movieService.fetchMovieDetailsById(1L);

        assertEquals(0, movieDtoWithDetails.getRating(), 0);
    }
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
        when(cacheLookupService.findMovieById(anyLong())).thenReturn(movie1);
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);

        movieService.updateMovie(1L, movieUpdateDto);
        verify(movieRepository).save(captor.capture());
        Movie updatedMovie = captor.getValue();

        verify(movieRepository, times(1)).save(any(Movie.class));
        assertEquals("Updated Title", updatedMovie.getTitle());
        assertEquals(MovieCategory.COMEDY, updatedMovie.getCategory());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenUpdateMovieAndDirectorNotFound(){
        when(cacheLookupService.findMovieById(anyLong())).thenReturn(movie1);
        when(directorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(1L, movieUpdateDto));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldNotUpdateCategoryWhenCategoryIsNull(){
        movieUpdateDto.setCategory(null);
        when(cacheLookupService.findMovieById(anyLong())).thenReturn(movie1);
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);

        movieService.updateMovie(1L, movieUpdateDto);
        verify(movieRepository).save(captor.capture());
        Movie updatedMovie = captor.getValue();

        assertEquals(MovieCategory.ACTION, updatedMovie.getCategory());
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyAscWithNoParametersInput(){
        setUpMovies();
        when(movieRepository.findAllWithRatesAsc()).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView(null, null, null);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyDescWithNoParametersInput(){
        setUpMovies();
        when(movieRepository.findAllWithRatesDesc()).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView(null, "DESC", null);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
        verify(movieRepository, times(1)).findAllWithRatesDesc();
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyAscWithTitleAndSortingInputAsc(){
        setUpMovies();
        when(movieRepository.findAllWithRatesByTitleAsc(anyString())).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView("movie", "ASC", null);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
        verify(movieRepository, times(1)).findAllWithRatesByTitleAsc(anyString());
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyAscWithTitleAndSortingInputDesc(){
        setUpMovies();
        when(movieRepository.findAllWithRatesByTitleDesc(anyString())).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView("movie", "DESC", null);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
        verify(movieRepository, times(1)).findAllWithRatesByTitleDesc(anyString());
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyAscWithTitleAndSortingAndCategoryInputAsc(){
        setUpMovies();
        when(movieRepository.findAllWithRatesByTitleAndCategoryAsc(anyString(), any())).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView("movie", "ASC", MovieCategory.COMEDY);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
        verify(movieRepository, times(1)).findAllWithRatesByTitleAndCategoryAsc(anyString(), any());
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyAscWithTitleAndSortingAndCategoryInputDesc(){
        setUpMovies();
        when(movieRepository.findAllWithRatesByTitleAndCategoryDesc(anyString(), any())).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView("movie", "DESC", MovieCategory.COMEDY);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
        verify(movieRepository, times(1)).findAllWithRatesByTitleAndCategoryDesc(anyString(), any());
    }
    @Test
    public void shouldFetchAllMoviesForViewCorrectlyAscWithTitleAndSortingAndCategoryAllInputDesc(){
        setUpMovies();
        when(movieRepository.findAllWithRatesByTitleDesc(anyString())).thenReturn(List.of(movie2, movie1));

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView("movie", "DESC", MovieCategory.ALL);

        assertEquals(2, movies.size());
        assertEquals("Another Movie", movies.getFirst().getTitle());
        verify(movieRepository, times(1)).findAllWithRatesByTitleDesc(anyString());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchAllMoviesForViewAndNothingFound(){
        setUpMovies();
        when(movieRepository.findAllWithRatesByTitleAndCategoryDesc(anyString(), any())).thenReturn(Collections.emptyList());

        List<MovieViewDto> movies = movieService.fetchAllMoviesForView("movie", "DESC", MovieCategory.COMEDY);

        assertEquals(0, movies.size());
        verify(movieRepository, times(1)).findAllWithRatesByTitleAndCategoryDesc(anyString(), any());
    }
    @Test
    public void shouldFetchMovieByIdVaadinCorrectly(){
        when(movieRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.of(movie1));

        MovieDto fetchedMovie = movieService.fetchMovieByIdVaadin(1L);

        assertEquals("Movie", fetchedMovie.getTitle());
        assertEquals(MovieCategory.ACTION, fetchedMovie.getCategory());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenFetchMovieByIdVaadinAndNoMovieFound(){
        when(movieRepository.findByIdWithDetails(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.fetchMovieByIdVaadin(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldUpdateMovieVaadinCorrectly(){
        movieDto.setTitle("Movie dto");
        movieDto.setActorIds(List.of(1L));
        director.setDirectorId(2L);
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie1));
        when(directorRepository.findById(anyLong())).thenReturn(Optional.of(director));
        when(actorRepository.findAllById(anyIterable())).thenReturn(List.of(actor));
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);

        movieService.updateMovieVaadin(1L, movieDto);

        verify(movieRepository).save(captor.capture());
        Movie capture = captor.getValue();
        assertEquals("Movie dto", capture.getTitle());
        assertEquals(2, capture.getDirector().getDirectorId());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenUpdateMovieVaadinAndNoMovieFound(){
        when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovieVaadin(1L, movieDto));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldNotUpdateDirectorWhenUpdateMovieVaadinAndNoDirectorProvided(){
        movieDto.setTitle("Movie dto");
        movieDto.setDirectorId(null);
        movieDto.setActorIds(new ArrayList<>());
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie1));
        when(actorRepository.findAllById(anyIterable())).thenReturn(List.of(actor));
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);

        movieService.updateMovieVaadin(1L, movieDto);

        verify(movieRepository).save(captor.capture());
        Movie capture = captor.getValue();
        assertEquals("Movie dto", capture.getTitle());
        assertEquals(1L, capture.getDirector().getDirectorId());
    }
    @Test
    public void shouldFetchRateByMovieIdAndUserId(){
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(movieRate));

        RateDto fetchedRate = movieService.fetchRateByMovieIdAndUserId(1L, 1L);

        assertEquals(1L, fetchedRate.getEntityId());
        assertEquals(4, fetchedRate.getRate());
    }
    @Test
    public void shouldReturnNullWhenFetchRateByMovieIdAndUserIdAndNoRateFound(){
        when(movieRateRepository.findByMovieIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        RateDto fetchedRate = movieService.fetchRateByMovieIdAndUserId(1L, 1L);

        assertNull(fetchedRate);
    }
    @Test
    public void shouldReturnTopRatedMoviesCorrectly(){
        movie1.setMovieRates(List.of(movieRate));
        movie2.setMovieRates(List.of(movieRate));
        when(movieRepository.findTopRatedMovies()).thenReturn(List.of(movie1, movie2));

        List<EntityWithRate> topRated = movieService.fetchTopRatedMovies();

        assertEquals(2, topRated.size());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchTopRatedMoviesCorrectlyAndNoMoviesFound(){

        when(movieRepository.findTopRatedMovies()).thenReturn(Collections.emptyList());

        List<EntityWithRate> topRated = movieService.fetchTopRatedMovies();

        assertEquals(0, topRated.size());
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
    private void setUpMovies(){
        movie1.setMovieRates(new ArrayList<>());
        movie2.setMovieRates(new ArrayList<>());
    }
}

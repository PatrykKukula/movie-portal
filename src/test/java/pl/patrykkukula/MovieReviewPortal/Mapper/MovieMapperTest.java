package pl.patrykkukula.MovieReviewPortal.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.*;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Model.MovieRate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieMapperTest {
    private MovieDto movieDto;
    private Movie movie;

    @BeforeEach
    public void setUp(){
        movieDto = MovieDto.builder()
                .title("Movie")
                .description("Movie description")
                .category(MovieCategory.COMEDY)
                .releaseDate(LocalDate.of(2000,1,1))
                .build();
        movieDto.setActorIds(new ArrayList<>());
        movie = createMovie();
    }

    @Test
    public void shouldMapMovieDtoToMovieCorrectly(){
        Movie mappedMovie = MovieMapper.mapMovieDtoToMovie(movieDto);

        assertEquals("Movie", mappedMovie.getTitle());
        assertEquals("Movie description", mappedMovie.getDescription());
        assertEquals(LocalDate.of(2000,1,1), mappedMovie.getReleaseDate());
        assertEquals(MovieCategory.COMEDY, mappedMovie.getCategory());
    }
    @Test
    public void shouldMapMovieToMovieDtoWithDetailsCorrectly(){
        MovieDtoWithDetails mappedMovie = MovieMapper.mapMovieToMovieDtoWithDetails(movie, 4.5, 100);

        assertEquals("Another Movie", mappedMovie.getTitle());
        assertEquals("Another Movie description", mappedMovie.getDescription());
        assertEquals("First name", mappedMovie.getDirector().getFirstName());
        assertEquals(1, mappedMovie.getActors().size());
        assertEquals(MovieCategory.DRAMA.toString(), mappedMovie.getCategory());
        assertEquals(LocalDate.of(2020, 1, 1), mappedMovie.getReleaseDate());
    }
    @Test
    public void shouldMapMovieToMovieDtoCorrectly(){
        MovieDto mappedMovie = MovieMapper.mapMovieToMovieDto(movie);

        assertEquals("Another Movie", mappedMovie.getTitle());
        assertEquals("Another Movie description", mappedMovie.getDescription());
        assertEquals(MovieCategory.DRAMA, mappedMovie.getCategory());
        assertEquals(LocalDate.of(2020, 1, 1), mappedMovie.getReleaseDate());
        assertEquals(1L, mappedMovie.getDirectorId());
        assertEquals(1, mappedMovie.getActorIds().size());
    }
    @Test
    public void shouldMapMovieToMovieViewCorrectly(){
        MovieViewDto mappedMovie = MovieMapper.mapMovieToMovieViewDto(movie);

        assertEquals("Another Movie", mappedMovie.getTitle());
        assertEquals(MovieCategory.DRAMA, mappedMovie.getCategory());
        assertEquals(LocalDate.of(2020, 1, 1), mappedMovie.getReleaseDate());
        assertEquals(1, mappedMovie.getRateNumber());
        assertEquals(5, mappedMovie.getAverageRate(), 0.01);
    }
    @Test
    public void shouldMapMovieDtoToMovieVaadinCorrectly(){
        Movie movie = MovieMapper.mapMovieDtoToMovieVaadin(movieDto, createMovie());

        assertEquals("Movie", movie.getTitle());
        assertEquals("Movie description", movie.getDescription());
        assertEquals(LocalDate.of(2000,1,1), movie.getReleaseDate());
    }
    @Test
    public void shouldMapMovieUpdateDtoToMovieCorrectly(){
        MovieUpdateDto movieUpdateDto = MovieUpdateDto.builder()
                .title("Updated Movie")
                .description("Updated description")
                .releaseDate(LocalDate.of(2020,1,1))
                .build();
        Movie mappedMovie = MovieMapper.mapMovieUpdateDtoToMovieUpdate(movieUpdateDto, movie);

        assertEquals("Updated Movie", mappedMovie.getTitle());
        assertEquals("Updated description", mappedMovie.getDescription());
        assertEquals(LocalDate.of(2020,1,1), mappedMovie.getReleaseDate());
    }
    @Test
    public void mapMovieToMovieDtoBasicCorrectly(){
        MovieDtoBasic mappedMovie = MovieMapper.mapToMovieDtoBasic(movie);

        assertEquals("Another Movie", mappedMovie.getTitle());
        assertEquals(MovieCategory.DRAMA.toString(), mappedMovie.getCategory());
        assertEquals(1L, mappedMovie.getId());
    }
    @Test
    public void shouldMapMovieRateToMovieDtoWithUserRateCorrectly(){
        MovieRate movieRate = MovieRate.builder()
                .movie(movie)
                .rate(4)
                .build();

        MovieDtoWithUserRate mappedMovie = MovieMapper.mapToMovieDtoWithUserRate(movieRate);

        assertEquals(4, mappedMovie.getUserRate());
        assertEquals("Another Movie", mappedMovie.getText());
    }
    @Test
    public void shouldMapMovieToMovieDtoWithAverageRateCorrectly(){

        MovieDtoWithUserRate mappedMovie = MovieMapper.mapToMovieDtoWithAverageRate(movie, 3.0);

        assertEquals(3, mappedMovie.getAverageRate(), 0.01);
        assertEquals("Another Movie", mappedMovie.getText());
    }

    private Movie createMovie() {
        Actor actor = Actor.builder().actorId(1L).firstName("First name").lastName("Last name").build();
        Director director = Director.builder().directorId(1L).firstName("First name").lastName("Last name").build();
        LocalDate date = LocalDate.of(2020, 1, 1);
        Movie movie = Movie.builder()
                .movieId(1L)
                .title("Another Movie")
                .description("Another Movie description")
                .releaseDate(date)
                .category(MovieCategory.DRAMA)
                .actors(List.of(actor))
                .director(director)
                .movieRates(List.of(MovieRate.builder().rate(5).movieRateId(1L).build()))
                .build();
        movie.setActors(List.of(actor));
        return movie;
    }
}


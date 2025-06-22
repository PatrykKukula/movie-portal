//package pl.patrykkukula.MovieReviewPortal.Mapper;
//import org.junit.jupiter.api.Test;
//import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
//import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
//import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
//import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieUpdateDto;
//import pl.patrykkukula.MovieReviewPortal.Model.Actor;
//import pl.patrykkukula.MovieReviewPortal.Model.Director;
//import pl.patrykkukula.MovieReviewPortal.Model.Movie;
//import java.time.LocalDate;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class MovieMapperTest {
//
//    @Test
//    public void shouldMapMovieToMovieDtoCorrectly(){
//        Movie movie = MovieMapper.mapToMovie(createMovieDto());
//
//        assertEquals("Movie", movie.getTitle());
//        assertEquals("Movie description", movie.getDescription());
//        assertEquals(LocalDate.of(2020,1,1), movie.getReleaseDate());
//    }
//    @Test
//    public void shouldMapMovieToMovieDtoWithDetailsCorrectly(){
//        MovieDtoWithDetails movieDtoWithDetails = MovieMapper.mapToMovieDtoWithDetails(createMovie(), 4.5);
//
//        assertEquals("Movie", movieDtoWithDetails.getTitle());
//        assertEquals("Movie description", movieDtoWithDetails.getDescription());
//        assertEquals("First name Last name", movieDtoWithDetails.getDirector());
//        assertEquals(1, movieDtoWithDetails.getActors().size());
//    }
//    @Test
//    public void shouldMapToMovieDtoCorrectly(){
//        MovieDto movieDto = MovieMapper.mapToMovieDto(createMovie());
//
//        assertEquals("Movie", movieDto.getTitle());
//        assertEquals(1L, movieDto.getId());
//    }
//    @Test
//    public void shouldMapMovieUpdateDtoToMovieDtoCorrectly(){
//        MovieUpdateDto movieUpdateDto = MovieUpdateDto.builder().title("Updated Title").build();
//
//        Movie movie = MovieMapper.mapToMovieUpdate(movieUpdateDto, createMovie());
//
//        assertEquals("Updated Title", movie.getTitle(), "should update title");
//        assertEquals("Movie description", movie.getDescription(), "should not update description");
//        assertEquals(LocalDate.of(2020,1,1), movie.getReleaseDate(), "should not update releaseDate");
//    }
//
//    private MovieDto createMovieDto() {
//        LocalDate date = LocalDate.of(2020, 1, 1);
//        return MovieDto.builder()
//                .title("Movie")
//                .description("Movie description")
//                .category("COMEDY")
//                .directorId(1L)
//                .releaseDate(date)
//                .build();
//    }
//    private Movie createMovie() {
//        Actor actor = Actor.builder().firstName("First name").lastName("Last name").build();
//        Director director = Director.builder().firstName("First name").lastName("Last name").build();
//        LocalDate date = LocalDate.of(2020, 1, 1);
//        return Movie.builder()
//                .movieId(1L)
//                .title("Movie")
//                .description("Movie description")
//                .releaseDate(date)
//                .category(MovieCategory.ACTION)
//                .actors(List.of(actor))
//                .director(director)
//                .build();
//    }
//}
//

package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.*;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRate.MovieRateDto;

import java.util.List;

public interface IMovieService {
    /*
        COMMON SECTION
     */
    Long addMovie(MovieDto movieDto);
    void deleteMovie(Long movieId);
    MovieDtoWithDetails fetchMovieDetailsById(Long movieId);
    /*
        REST API SECTION
     */
    void updateMovie(Long movieId, MovieUpdateDto movieDto);
    List<MovieDtoBasic> fetchAllMoviesByTitle(String title, String sorted);
    List<MovieDtoBasic> fetchAllMovies(String sorted);
    Long addRateToMovie(MovieRateDto movieRateDto);
    boolean addActorToMovie(Long movieId, Long actorId);
    boolean removeActorFromMovie(Long movieId, Long actorId);
    boolean removeRate(Long movieId);
    /*
        VAADIN VIEW SECTION
     */
    List<MovieViewDto> fetchAllMoviesForView(String title);
    List<MovieViewDto> fetchAllMoviesForViewByCategory(MovieCategory category, String title);
    MovieDto fetchMovieByIdVaadin(Long movieId);
    void updateMovieVaadin(Long movieId, MovieDto movieDto);
}

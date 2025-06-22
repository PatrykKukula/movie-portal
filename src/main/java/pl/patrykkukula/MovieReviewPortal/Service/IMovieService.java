package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRate.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieUpdateDto;

import java.util.List;

public interface IMovieService {

    Long addMovie(MovieDto movieDto, List<Long> ids);
    void deleteMovie(Long movieId);
    boolean addActorToMovie(Long movieId, Long actorId);
    boolean removeActorFromMovie(Long movieId, Long actorId);
    MovieDtoWithDetails fetchMovieDetailsById(Long movieId);
    MovieDto fetchMovieById(Long movieId);
    List<MovieDtoBasic> fetchAllMoviesByTitle(String title, String sorted);
    List<MovieDtoBasic> fetchAllMovies(String sorted);
    void updateMovie(Long movieId, MovieDto movieDto, List<Long> ids);
    Long addRateToMovie(MovieRateDto movieRateDto);
    boolean removeRate(Long movieId);
}

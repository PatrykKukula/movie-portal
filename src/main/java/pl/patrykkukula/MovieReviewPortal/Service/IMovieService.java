package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.MovieUpdateDto;

import java.util.List;

public interface IMovieService {

    Long addMovie(MovieDto movieDto);
    void deleteMovie(Long movieId);
    boolean addActorToMovie(Long movieId, Long actorId);
    boolean removeActorFromMovie(Long movieId, Long actorId);
    MovieDtoWithDetails fetchMovieById(Long movieId);
    List<MovieDtoBasic> fetchAllMoviesByTitle(String title, String sorted);
    List<MovieDtoBasic> fetchAllMovies(String sorted);
    void updateMovie(Long movieId, MovieUpdateDto movieDto);
    void addRateToMovie(MovieRateDto movieRateDto);
    void removeRate(Long movieId);
}

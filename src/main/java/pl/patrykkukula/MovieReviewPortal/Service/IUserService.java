package pl.patrykkukula.MovieReviewPortal.Service;

import org.springframework.data.domain.Page;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import java.util.List;

public interface IUserService {
    Double fetchAverageRate(Long userId, String entityType);
    MovieCategory fetchMostRatedCategory(Long userId);
    List<MovieDtoWithUserRate> fetch5HighestRatedMovies(Long userId);
    List<ActorDtoWithUserRate> fetch5HighestRatedActors(Long userId);
    List<DirectorDtoWithUserRate> fetch5HighestRatedDirectors(Long userId);
    Long fetchMovieRateCount(Long userId);
    Long fetchActorRateCount(Long userId);
    Long fetchDirectorRateCount(Long userId);
    Page<MovieDtoWithUserRate> fetchAllRatedMovies(Long userId, Integer pageNo, Integer pageSize);
    Page<ActorDtoWithUserRate> fetchAllRatedActors(Long userId, Integer pageNo, Integer pageSize);
    Page<DirectorDtoWithUserRate> fetchAllRatedDirectors(Long userId, Integer pageNo, Integer pageSize);
}

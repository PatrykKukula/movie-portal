package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

public interface ICacheLookupService {

    Movie findMovieById(Long movieId);
    Actor findActorById(Long actorId);
    Director findDirectorById(Long directorId);
}

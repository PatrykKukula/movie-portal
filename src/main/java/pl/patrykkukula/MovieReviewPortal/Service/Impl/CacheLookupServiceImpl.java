package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Service.ICacheLookupService;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;

@Service
@RequiredArgsConstructor
public class CacheLookupServiceImpl implements ICacheLookupService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;

    @Override
    @Cacheable(value = "movie")
    public Movie findMovieById(Long movieId) {
        validateId(movieId);
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie", String.valueOf(movieId)));
    }
    @Override
    @Cacheable(value = "actor")
    public Actor findActorById(Long actorId) {
        validateId(actorId);
        return actorRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", String.valueOf(actorId)));
    }
    @Override
    @Cacheable(value = "director")
    public Director findDirectorById(Long directorId) {
        validateId(directorId);
        return directorRepository.findById(directorId).orElseThrow(() -> new ResourceNotFoundException("Director", "actor id", String.valueOf(directorId)));
    }
}

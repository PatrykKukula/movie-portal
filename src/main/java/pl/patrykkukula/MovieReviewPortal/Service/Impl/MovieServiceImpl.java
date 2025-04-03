package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.*;
import pl.patrykkukula.MovieReviewPortal.Service.IMovieService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements IMovieService {

    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;
    private final UserEntityRepository userEntityRepository;
    private final MovieRateRepository movieRateRepository;

    @Override
    @Transactional
    public Long addMovie(MovieDto movieDto) {
        Movie movie = mapToMovie(movieDto);
        movie.setCategory(findCategory(movieDto.getCategory()));
        movie.setDirector(findDirector(movieDto.getDirectorId()));
        Movie savedMovie = movieRepository.save(movie);
        return savedMovie.getMovieId();
    }
    @Override
    public void deleteMovie(Long movieId) {
        validateId(movieId);
        movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie", "Movie id", String.valueOf(movieId)));
        movieRepository.deleteById(movieId);
    }
    @Override
    @Transactional
    public boolean addActorToMovie(Long movieId, Long actorId) {
        validateId(movieId);
        validateId(actorId);
        Actor actor = actorRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("Actor", "actor id", String.valueOf(actorId)));
        Movie movie = movieRepository.findByIdWithActors(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        if (!movie.getActors().contains(actor)) {
            movie.getActors().add(actor);
            movieRepository.save(movie);
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    public boolean removeActorFromMovie(Long movieId, Long actorId) {
        validateId(movieId);
        validateId(actorId);
        Movie movie = movieRepository.findByIdWithActors(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        boolean removed = movie.getActors().removeIf(actor -> actor.getActorId().equals(actorId));
        movieRepository.save(movie);
        return removed;
    }
    @Override
    public MovieDtoWithDetails fetchMovieById(Long movieId) {
        validateId(movieId);
        Tuple data = movieRepository.findByIdWithActorsAndMovieRates(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        Movie movie = data.get("movie", Movie.class);
        Double rate = data.get("rating", Double.class);
        return mapToMovieDtoWithDetails(movie, rate);
    }
    @Override
    public List<MovieDtoBasic> fetchAllMoviesByTitle(String title, String sorted) {
        String validatedSorted = validateSorting(sorted);
        return validatedSorted.equals("ASC") ?
                movieRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(title).stream().map(MovieMapper::mapToMovieDtoBasic).toList() :
                movieRepository.findByTitleContainingIgnoreCaseOrderByTitleDesc(title).stream().map(MovieMapper::mapToMovieDtoBasic).toList();
    }
    @Override
    public List<MovieDtoBasic> fetchAllMovies(String sorted) {
        String validatedSorted = validateSorting(sorted);
        return validatedSorted.equals("ASC") ?
                movieRepository.findAllOrderByTitleAsc().stream().map(MovieMapper::mapToMovieDtoBasic).toList() :
                movieRepository.findAllOrderByTitleDesc().stream().map(MovieMapper::mapToMovieDtoBasic).toList();
    }
    @Override
    @Transactional
    public void updateMovie(Long movieId, MovieUpdateDto movieDto) {
        validateId(movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie", String.valueOf(movieId)));
        Long directorId = movieDto.getDirectorId();
        if (directorId != null) {
            validateId(directorId);
            Director director = findDirector(directorId);
            movie.setDirector(director);
        }
        String category = movieDto.getCategory();
        if (category != null) {
            movie.setCategory(findCategory(category));
        }
        movieRepository.save(mapToMovieUpdate(movieDto, movie));
    }
    @Override
    @Transactional
    public void addRateToMovie(MovieRateDto movieRateDto) {
        validateId(movieRateDto.getMovieId());
        UserEntity user = getUserEntity();
        Movie movie = movieRepository.findByIdWithMovieRates(movieRateDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("MovieRate", "movie", String.valueOf(movieRateDto.getMovieId())));
        Optional<MovieRate> optCurrentRate = movieRateRepository.findByMovieIdAndUserId(movieRateDto.getMovieId(), user.getUserId());
        if (optCurrentRate.isPresent()) {
            MovieRate currentRate = optCurrentRate.get();
            currentRate.setRate(movieRateDto.getRate());
            movieRateRepository.save(currentRate);
        } else {
            MovieRate movieRate = MovieRate.builder()
                    .movie(movie)
                    .user(user)
                    .rate(movieRateDto.getRate())
                    .build();
            movieRateRepository.save(movieRate);
        }
    }
    @Override
    @Transactional
    public void removeRate(Long movieId) {
        validateId(movieId);
        UserEntity user = getUserEntity();
        int deleted = movieRateRepository.deleteByMovieIdAndUserId(movieId, user.getUserId());

        if (deleted == 0) throw new IllegalResourceModifyException("You didn't set score for this movie");

    }
    private MovieCategory findCategory(String category) {
        return Arrays.stream(MovieCategory.values())
                .filter(cat -> cat.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category", category));
    }
    private Director findDirector(Long directorId) {
        return directorRepository.findById(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("Director", "director id", String.valueOf(directorId)));
    }
    private UserEntity getUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("User is not logged in");
        }
        User user = (User) auth.getPrincipal();
        return userEntityRepository.findByEmail(user.getUsername()).orElseThrow(() -> new ResourceNotFoundException("Account", "email", user.getUsername()));
    }
}

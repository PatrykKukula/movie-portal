package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.*;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRate.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.*;
import pl.patrykkukula.MovieReviewPortal.Service.IMovieService;

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

    /*
        COMMON SECTION
     */
    @Override
    public MovieDtoWithDetails fetchMovieDetailsById(Long movieId) {
        validateId(movieId);
        Movie movie = movieRepository.findByIdWithDetails(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        Double rate = movieRateRepository.getAverageMovieRate(movieId);
        Integer rateNumber = movieRepository.countMovieRates(movieId);
        return mapToMovieDtoWithDetails(movie, rate, rateNumber);
    }

    @Override
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
        MovieCategory category = movieDto.getCategory();
        if (category != null) {
            movie.setCategory(findCategory(category));
        }
        movieRepository.save(mapMovieUpdateDtoToMovieUpdate(movieDto, movie));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Long addMovie(MovieDto movieDto) {
        Movie movie = mapToMovie(movieDto);
        movie.setCategory(findCategory(movieDto.getCategory()));
        movie.setDirector(findDirector(movieDto.getDirectorId()));
        movie.setActors(actorRepository.findAllById(movieDto.getActorIds()));
        Movie savedMovie = movieRepository.save(movie);
        return savedMovie.getMovieId();
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMovie(Long movieId) {
        validateId(movieId);
        movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie", "Movie id", String.valueOf(movieId)));
        movieRepository.deleteById(movieId);
    }
    /*
        REST API SECTION
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public boolean removeActorFromMovie(Long movieId, Long actorId) {
        validateId(movieId);
        validateId(actorId);
        Movie movie = movieRepository.findByIdWithActors(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        boolean removed = movie.getActors().removeIf(actor -> actor.getActorId().equals(actorId));
        movieRepository.save(movie);
        return removed;
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
    @PreAuthorize("hasAnyRole()")
    public Long addRateToMovie(MovieRateDto movieRateDto) {
        validateId(movieRateDto.getMovieId());
        UserEntity user = getUserEntity();
        Movie movie = movieRepository.findByIdWithMovieRates(movieRateDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("MovieRate", "movie", String.valueOf(movieRateDto.getMovieId())));
        Optional<MovieRate> optCurrentRate = movieRateRepository.findByMovieIdAndUserId(movieRateDto.getMovieId(), user.getUserId());
        if (optCurrentRate.isPresent()) {
            MovieRate currentRate = optCurrentRate.get();
            currentRate.setRate(movieRateDto.getRate());
            MovieRate updatedRate = movieRateRepository.save(currentRate);
            return updatedRate.getMovieRateId();
        } else {
            MovieRate movieRate = MovieRate.builder()
                    .movie(movie)
                    .user(user)
                    .rate(movieRateDto.getRate())
                    .build();
            MovieRate addedRate = movieRateRepository.save(movieRate);
            return addedRate.getMovieRateId();
        }
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole()")
    public boolean removeRate(Long movieId) {
        validateId(movieId);
        UserEntity user = getUserEntity();
        int deleted = movieRateRepository.deleteByMovieIdAndUserId(movieId, user.getUserId());

        return deleted > 0;
    }

    /*
       VAADIN VIEW SECTION
   */
    @Override
    public List<MovieViewDto> fetchAllMoviesForView(String title) {
        return title.isEmpty() ? movieRepository.findAllWithMovieRates().stream().map(
                movie -> MovieMapper.mapToMovieViewDto(movie, movie.averageMovieRate(), movie.movieRatesNumber())).toList() :
                movieRepository.findAllWithMovieRatesByTitle(title).stream().map(
                        movie -> MovieMapper.mapToMovieViewDto(movie, movie.averageMovieRate(), movie.movieRatesNumber())).toList();
    }
    @Override
    public List<MovieViewDto> fetchAllMoviesForViewByCategory(MovieCategory category, String title) {
        return title.isEmpty() ? movieRepository.findAllWithMovieRatesByCategory(category).stream().map(
                movie -> MovieMapper.mapToMovieViewDto(movie, movie.averageMovieRate(), movie.movieRatesNumber())).toList() :
                movieRepository.findAllWithMovieRatesByCategoryByTitle(category,title).stream().map(
                        movie -> MovieMapper.mapToMovieViewDto(movie, movie.averageMovieRate(), movie.movieRatesNumber())).toList();
    }
    @Override
    public MovieDto fetchMovieByIdVaadin(Long movieId) {
        validateId(movieId);
        Movie movie = movieRepository.findByIdWithDetails(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        return mapToMovieDto(movie);
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateMovieVaadin(Long movieId, MovieDto movieDto) {
        validateId(movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie", String.valueOf(movieId)));
        Long directorId = movieDto.getDirectorId();
        if (directorId != null) {
            validateId(directorId);
            Director director = findDirector(directorId);
            movie.setDirector(director);
        }
        MovieCategory category = movieDto.getCategory();
        if (category != null) {
            movie.setCategory(findCategory(category));

        }
        movie.setActors(actorRepository.findAllById(movieDto.getActorIds()));

        movieRepository.save(mapMovieDtoToMovie(movieDto, movie));
    }

    private MovieCategory findCategory(MovieCategory category) {
        return MovieCategory.valueOf(category.name());
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

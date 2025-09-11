package pl.patrykkukula.MovieReviewPortal.Service.Impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.ActorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRateRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.MovieRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IMovieService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements IMovieService {
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final ActorRepository actorRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final MovieRateRepository movieRateRepository;
    private final CacheLookupServiceImpl cacheLookupService;
    /*
        COMMON SECTION
     */
    @Override
    @Cacheable(value = "movie-details")
    public MovieDtoWithDetails fetchMovieDetailsById(Long movieId) {
        validateId(movieId);
        Movie movie = movieRepository.findByIdWithDetails(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        Double rate = movieRateRepository.getAverageMovieRate(movieId);
        Integer rateNumber = movieRepository.countMovieRates(movieId);
        return mapMovieToMovieDtoWithDetails(movie, rate, rateNumber);
    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @CacheEvict(value = "all-movies", allEntries = true)
    public Long addMovie(MovieDto movieDto) {
        Movie movie = mapMovieDtoToMovie(movieDto);
        movie.setCategory(findCategory(movieDto.getCategory()));
        movie.setDirector(findDirector(movieDto.getDirectorId()));
        movie.setActors(actorRepository.findAllById(movieDto.getActorIds()));
        Movie savedMovie = movieRepository.save(movie);
        return savedMovie.getMovieId();
    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @CacheEvict(value = "movie-details")
    public void deleteMovie(Long movieId) {
        validateId(movieId);
        cacheLookupService.findMovieById(movieId);
        movieRepository.deleteById(movieId);
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = "movie-details", key = "#rateDto.entityId")
    public RatingResult addRateToMovie(RateDto rateDto) {
        validateId(rateDto.getEntityId());
        Optional<UserEntity> user = userDetailsService.getLoggedUserEntity();
        if (user.isPresent()) {
            Optional<MovieRate> optCurrentRate = movieRateRepository.findByMovieIdAndUserId(rateDto.getEntityId(), user.get().getUserId());
            if (optCurrentRate.isPresent()) {
                MovieRate currentRate = optCurrentRate.get();
                currentRate.setRate(rateDto.getRate());
                MovieRate updatedRate = movieRateRepository.save(currentRate);
                return new RatingResult(updatedRate.getMovie().averageMovieRate(), true);
            } else {
                Movie movie = movieRepository.findByIdWithMovieRates(rateDto.getEntityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie", "movieId", String.valueOf(rateDto.getEntityId())));
                MovieRate movieRate = MovieRate.builder()
                        .movie(movie)
                        .user(user.get())
                        .rate(rateDto.getRate())
                        .build();
                MovieRate addedRate = movieRateRepository.save(movieRate);
                movie.getMovieRates().add(addedRate);
                return new RatingResult(addedRate.getMovie().averageMovieRate(), false);
            }
        }
        throw new AccessDeniedException("Log in to add rate");
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = "movie-details")
    public Double removeRate(Long movieId) {
        validateId(movieId);
        Optional<UserEntity> user = userDetailsService.getLoggedUserEntity();
        if(user.isPresent()){
            int deletedRows = movieRateRepository.deleteByMovieIdAndUserId(movieId, user.get().getUserId());
            if (deletedRows == 1) {
                Movie movie = movieRepository.findByIdWithMovieRates(movieId).orElseThrow(() -> new ResourceNotFoundException("MovieRate", "movie", String.valueOf(movieId)));
                return movie.averageMovieRate();
            }
            else throw new IllegalStateException("Didn't remove rate successfully. Please try again");
        }
        throw new AccessDeniedException("Log in to remove rate");
    }
    /*
        REST API SECTION
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @CacheEvict(value = "movie-details", key = "#movieId")
    public void updateMovie(Long movieId, MovieUpdateDto movieDto) {
        validateId(movieId);
        Movie movie = cacheLookupService.findMovieById(movieId);
        Long directorId = movieDto.getDirectorId();
        if (directorId != null) {
            validateId(directorId);
            Director director = findDirector(directorId);
            movie.setDirector(director);
        }
        MovieCategory category = movieDto.getCategory();
        if (category != null && !category.equals(MovieCategory.ALL)) {
            movie.setCategory(findCategory(category));
        }
        movieRepository.save(mapMovieUpdateDtoToMovieUpdate(movieDto, movie));
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Caching(
            evict = {
                    @CacheEvict(value = "movie-details", key = "#movieId"),
                    @CacheEvict(value = "actor, actor-details", key = "#actorId")
            }
    )
    public boolean addActorToMovie(Long movieId, Long actorId) {
        validateId(movieId);
        validateId(actorId);
        Actor actor = cacheLookupService.findActorById(actorId);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Caching(
            evict = {
                    @CacheEvict(value = "movie-details", key = "#movieId"),
                    @CacheEvict(value = "actor, actor-details", key = "#actorId")
            }
    )
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
    @Cacheable(value = "all-movies")
    public List<MovieDtoBasic> fetchAllMovies(String sorted) {
        String validatedSorted = validateSorting(sorted);
        return validatedSorted.equals("ASC") ?
                movieRepository.findAllOrderByTitleAsc().stream().map(MovieMapper::mapToMovieDtoBasic).toList() :
                movieRepository.findAllOrderByTitleDesc().stream().map(MovieMapper::mapToMovieDtoBasic).toList();
    }
    /*
       VAADIN VIEW SECTION
   */
    @Override
    @Cacheable(value = "all-movies-view", unless = "#result.isEmpty() or #title == null or #title.length() <= 3")
    public List<MovieViewDto> fetchAllMoviesForView(String title, String sorting, MovieCategory category) {
        String validatedSorting = validateSorting(sorting);

        if (title == null && validatedSorting.equals("ASC") && (category == null || category == MovieCategory.ALL)) return movieRepository.findAllWithRatesAsc().stream()
                .map(MovieMapper::mapMovieToMovieViewDto).toList();
        else if (title == null && validatedSorting.equals("DESC") && (category == null || category == MovieCategory.ALL)) return movieRepository.findAllWithRatesDesc().stream()
                .map(MovieMapper::mapMovieToMovieViewDto).toList();
        else if (title != null && validatedSorting.equals("ASC") && (category == null || category == MovieCategory.ALL)) return movieRepository.findAllWithRatesByTitleAsc(title).stream()
                .map(MovieMapper::mapMovieToMovieViewDto).toList();
        else if (title != null && validatedSorting.equals("DESC") && (category == null || category == MovieCategory.ALL)) return movieRepository.findAllWithRatesByTitleDesc(title).stream()
                .map(MovieMapper::mapMovieToMovieViewDto).toList();
        else if (title != null && validatedSorting.equals("ASC")) return movieRepository.findAllWithRatesByTitleAndCategoryAsc(title, category)
                .stream().map(MovieMapper::mapMovieToMovieViewDto).toList();
        else return movieRepository.findAllWithRatesByTitleAndCategoryDesc(title, category)
                .stream().map(MovieMapper::mapMovieToMovieViewDto).toList();
    }
    @Override
    @Cacheable(value = "movies-dto")
    public MovieDto fetchMovieByIdVaadin(Long movieId) {
        validateId(movieId);
        Movie movie = movieRepository.findByIdWithDetails(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "movie id", String.valueOf(movieId)));
        return mapMovieToMovieDto(movie);
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
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

        movieRepository.save(mapMovieDtoToMovieVaadin(movieDto, movie));
    }
    @Override
    public RateDto fetchRateByMovieIdAndUserId(Long movieId, Long userId) {
        Optional<MovieRate> optionalMovieRate = movieRateRepository.findByMovieIdAndUserId(movieId, userId);
        if (optionalMovieRate.isPresent()) {
            MovieRate movieRate = optionalMovieRate.get();
            return RateDto.builder()
                    .entityId(movieRate.getMovieRateId())
                    .rate(movieRate.getRate())
                    .build();
        }
        return null;
    }
    @Override
    @Cacheable("top-rated-movies")
    public List<EntityWithRate> fetchTopRatedMovies() {
        return movieRepository.findTopRatedMovies().stream().map(movie -> (EntityWithRate)MovieMapper.mapToMovieDtoWithAverageRate(movie, movie.averageMovieRate())).toList();
    }
    private MovieCategory findCategory(MovieCategory category) {
        if (category.equals(MovieCategory.ALL)) throw new InputMismatchException("Category \"ALL\" cannot be set. Please choose valid category");
        return MovieCategory.valueOf(category.name());
    }
    private Director findDirector(Long directorId) {
        return directorRepository.findById(directorId)
                .orElseThrow(() -> new ResourceNotFoundException("Director", "director id", String.valueOf(directorId)));
    }
}

package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IUserService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserEntityRepository userRepository;

    @Override
    public UserEntity loadUserEntityById(Long userId) {
        return userRepository.findByIdWithComments(userId)
                    .orElse(null);
    }
    @Override
    public UserEntity loadUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
    @Override
    public String getUsername(String email) {
        UserEntity user = userRepository.findByEmailWithRoles(email).orElseThrow(() -> new UsernameNotFoundException("Account with email " + email + " not found"));
        return user.getUsername();
    }
    @Override
    public Double fetchAverageRate(Long userId, String entityType) {
        return userRepository.findAverageMovieRate(userId);
    }
    @Override
    public MovieCategory fetchMostRatedCategory(Long userId) {
        List<MovieCategory> movieRates = userRepository.findUserMovieRates(userId);
        return movieRates.isEmpty() ? null : movieRates.getFirst();
    }
    @Override
    public List<MovieDtoWithUserRate> fetchHighestRatedMoviesByUser(Long userId) {
          return userRepository.find5TopRatedMovies(userId).stream().map(MovieMapper::mapToMovieDtoWithUserRate).toList();
    }
    @Override
    public List<ActorDtoWithUserRate> fetchHighestRatedActorsByUser(Long userId) {
        return userRepository.find5TopRatedActors(userId).stream().map(ActorMapper::mapToActorDtoWithUserRate).toList();
    }
    @Override
    public List<DirectorDtoWithUserRate> fetchHighestRatedDirectorsByUser(Long userId) {
        return userRepository.find5TopRatedDirectors(userId).stream().map(DirectorMapper::mapToDirectorDtoWithUserRate).toList();
    }
    @Override
    public Long fetchMovieRateCount(Long userId) {
        return userRepository.findMovieRateCount(userId);
    }
    @Override
    public Long fetchActorRateCount(Long userId) {
        return userRepository.findActorRateCount(userId);
    }
    @Override
    public Long fetchDirectorRateCount(Long userId) {
        return userRepository.findDirectorRateCount(userId);
    }
    @Override
    public Page<MovieDtoWithUserRate> fetchAllRatedMovies(Long userId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "rate"));
        return userRepository.findAllRatedMovies(userId, page).map(MovieMapper::mapToMovieDtoWithUserRate);
    }
    @Override
    public Page<ActorDtoWithUserRate> fetchAllRatedActors(Long userId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "rate"));
        return userRepository.findAllRatedActors(userId, page).map(ActorMapper::mapToActorDtoWithUserRate);
    }
    @Override
    public Page<DirectorDtoWithUserRate> fetchAllRatedDirectors(Long userId, Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "rate"));
        return userRepository.findAllRatedDirectors(userId, page).map(DirectorMapper::mapToDirectorDtoWithUserRate);
    }
}

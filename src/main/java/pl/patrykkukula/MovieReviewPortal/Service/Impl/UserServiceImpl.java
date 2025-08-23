package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Mapper.ActorMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.MovieMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.UserMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.RoleRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IUserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserEntityRepository userRepository;
    private final RoleRepository roleRepository;

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
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean banUser(String username, Duration banTime) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()){
            UserEntity user = userOpt.get();
            user.setBanned(true);
            if (user.getBanExpiration() != null && user.getBanExpiration().isAfter(LocalDateTime.now())){
                user.setBanExpiration(user.getBanExpiration().plus(banTime));
            }
            else {
                user.setBanExpiration(LocalDateTime.now().plus(banTime));
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean removeBan(UserEntity user) {
        if (user != null) {
            user.setBanned(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean addRole(String username, String roleName) {
        Optional<UserEntity> userOpt = userRepository.findByUsernameWithRoles(username);
        if (userOpt.isPresent()){
            UserEntity user = userOpt.get();
            Role role = roleRepository.findRoleByRoleName(roleName).orElseThrow(() -> new RuntimeException("Role not found. Please contact technical support"));
            List<Role> roles = user.getRoles();
            if (roles.stream().anyMatch(r -> r.getRoleName().equals("MODERATOR"))) throw new IllegalStateException("User already has Moderator role");
            roles.add(role);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean removeRole(String username, String roleName) {
        Optional<UserEntity> userOpt = userRepository.findByUsernameWithRoles(username);
        if (userOpt.isPresent()){
            UserEntity user = userOpt.get();
            Role role = roleRepository.findRoleByRoleName(roleName).orElseThrow(() -> new RuntimeException("Role USER not found. Please contact technical support"));
            List<Role> roles = user.getRoles();
            if (roles.stream().noneMatch(r -> r.getRoleName().equals("MODERATOR"))) throw new IllegalStateException("User doesn't have Moderator role");
            roles.remove(role);
            userRepository.save(user);
            return true;
            }
        return false;
    }
    @Override
    public Integer countUsers(String username) {
        return (username == null || username.isEmpty()) ? (int)userRepository.count() : userRepository.countUsersByUsername(username);
    }
    @Override
    public Page<UserDataDto> fetchAllUsers(int pageNo, int pageSize, Sort sort, String searchText) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return (searchText ==  null || searchText.isEmpty()) ? userRepository.findAllWithRoles(pageable).map(UserMapper::mapToUserDataDto) :
                userRepository.findAllWithRolesByUsername(pageable, searchText).map(UserMapper::mapToUserDataDto);
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
          return userRepository.findTopRatedMovies(userId).stream().map(MovieMapper::mapToMovieDtoWithUserRate).toList();
    }
    @Override
    public List<ActorDtoWithUserRate> fetchHighestRatedActorsByUser(Long userId) {
        return userRepository.findTopRatedActors(userId).stream().map(ActorMapper::mapToActorDtoWithUserRate).toList();
    }
    @Override
    public List<DirectorDtoWithUserRate> fetchHighestRatedDirectorsByUser(Long userId) {
        return userRepository.findTopRatedDirectors(userId).stream().map(DirectorMapper::mapToDirectorDtoWithUserRate).toList();
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

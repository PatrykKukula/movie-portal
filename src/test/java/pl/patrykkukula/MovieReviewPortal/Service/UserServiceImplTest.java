package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.BanDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.*;
import pl.patrykkukula.MovieReviewPortal.Repository.RoleRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserEntityRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private BanDto banDto;
    private UserEntity user;
    private Role role;
    private Role moderator;
    private List<Role> roles = new ArrayList<>();
    private Pageable pageable;
    private Sort sort;
    private Movie movie;
    private Actor actor;
    private Director director;
    private MovieRate movieRate;
    private ActorRate actorRate;
    private DirectorRate directorRate;
    private final LocalDate date = LocalDate.of(2000,12,12);
    private List<Actor> actors = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        moderator = Role.builder().roleName("MODERATOR").build();
        role = Role.builder().roleName("USER").build();
        roles.add(role);
        banDto = new BanDto("Username", "7 days");
        user = UserEntity.builder()
                .userId(1L)
                .username("Username")
                .roles(roles)
                .banned(false)
                .banExpiration(null)
                .build();
        sort = Sort.by(Sort.Direction.ASC, "username");
        pageable = PageRequest.of(0,2, sort);
        actor = Actor.builder()
                .actorId(1L)
                .firstName("Alex")
                .lastName("Smith")
                .movies(Collections.emptyList())
                .actorRates(new ArrayList<>())
                .build();
        director = Director.builder()
                .directorId(1L)
                .firstName("John")
                .lastName("Smith")
                .movies(Collections.emptyList())
                .build();
        movie = Movie.builder()
                .movieId(1L)
                .title("Movie")
                .description("Description")
                .releaseDate(date)
                .category(MovieCategory.ACTION)
                .actors(actors)
                .director(director)
                .build();
        movieRate = MovieRate.builder()
                .movieRateId(1L)
                .movie(movie)
                .user(user)
                .rate(4)
                .build();
        actorRate = ActorRate.builder()
                .actor(actor)
                .rate(5)
                .build();
        directorRate = DirectorRate.builder()
                .director(director)
                .rate(1)
                .build();
    }
    @Test
    public void shouldBanUserCorrectly(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        boolean banned = userService.banUser(banDto);
        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertTrue(banned);
        assertEquals(true, savedUser.getBanned());
        assertEquals(LocalDateTime.now().plusDays(7L).toString().substring(0,15), savedUser.getBanExpiration().toString().substring(0,15));
    }
    @Test
    public void shouldProlongBanCorrectlyWhenBanUser(){
        user.setBanned(true);
        user.setBanExpiration(LocalDateTime.now());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        boolean banned = userService.banUser(banDto);
        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertTrue(banned);
        assertEquals(LocalDateTime.now().plusDays(7L).toString().substring(0,16), savedUser.getBanExpiration().toString().substring(0,16));
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenBanUserWithInvalidBanDuration(){
        banDto.setBanDuration("Invalid duration");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.banUser(banDto));
        assertEquals("Choose valid ban duration: 24 hours, 7 days, 30 days or permanent", ex.getMessage());
    }
    @Test
    public void shouldReturnFalseWhenNotFoundExceptionWhenBanUserAndUserNotFound(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        boolean banned = userService.banUser(banDto);

        assertFalse(banned);
    }
    @Test
    public void shouldRemoveBanCorrectly(){
        user.setBanned(true);
        user.setBanExpiration(LocalDateTime.now());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        userService.removeBan("User");

        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertFalse(savedUser.getBanned());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenRemoveBanAndUserNotFound(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.removeBan("User"));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldAddRoleCorrectly(){
        when(userRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByRoleName(anyString())).thenReturn(Optional.of(Role.builder().roleName("MODERATOR").build()));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        userService.addRole("Username", "MODERATOR");

        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertEquals(2L, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream().anyMatch(role -> role.getRoleName().equals("MODERATOR")));
    }
    @Test
    public void shouldReturnFalseWhenAddRoleAndUserNotFound(){
        when(userRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.empty());

        boolean added = userService.addRole("User", "MODERATOR");

        assertFalse(added);
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenAddRoleAndRoleIsInvalid(){
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.addRole("User", "invalid"));
        assertEquals("Choose valid role: MODERATOR", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenAddRoleAndUserAlreadyHasRole(){
        user.setRoles(List.of(Role.builder().roleName("MODERATOR").build()));
        when(userRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByRoleName(anyString())).thenReturn(Optional.of(role));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.addRole("User", "MODERATOR"));
        assertEquals("User already has Moderator role", ex.getMessage());
    }
    @Test
    public void shouldRemoveRoleCorrectly(){
        roles.remove(role);
        roles.add(moderator);
        user.setRoles(roles);
        when(userRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByRoleName(anyString())).thenReturn(Optional.of(moderator));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        userService.removeRole("Username", "MODERATOR");
        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();

        assertEquals(0, savedUser.getRoles().size());
    }
    @Test
    public void shouldReturnFalseWhenRemoveRoleAndUserNotFound(){
        when(userRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.empty());

        boolean removed = userService.removeRole("User", "Role");

        assertFalse(removed);
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenRemoveRoleAndUserDoesNotHaveRole(){
        when(userRepository.findByUsernameWithRoles(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByRoleName(anyString())).thenReturn(Optional.of(moderator));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> userService.removeRole("User", "MODERATOR"));
        assertEquals("You can only remove MODERATOR role. If you tried to remove MODERATOR role and see this response, user doesn't have that role", ex.getMessage());
    }
    @Test
    public void shouldReturnUserCountCorrectly(){
        when(userRepository.count()).thenReturn(999L);

        Integer count = userService.countUsers(null);

        assertEquals(999, count);
    }
    @Test
    public void shouldReturnUserCountCorrectlyWithUsernameInput(){
        when(userRepository.countUsersByUsername(anyString())).thenReturn(999);

        Integer count = userService.countUsers("User");

        assertEquals(999, count);
    }
    @Test
    public void shouldFetchAllUsersCorrectly(){
        when(userRepository.findAllWithRoles(any(Pageable.class))).thenReturn(setUpUserList());

        Page<UserDataDto> users = userService.fetchAllUsers(0, 2, sort, null);
        assertEquals(6L, users.getTotalElements());
        assertEquals(3, users.getTotalPages());
    }
    @Test
    public void shouldFetchAllUsersByUsernameCorrectly(){
        when(userRepository.findAllWithRolesByUsername(any(Pageable.class), anyString())).thenReturn(setUpUserList());

        Page<UserDataDto> users = userService.fetchAllUsers(0, 2, sort, "username");
        assertEquals(6L, users.getTotalElements());
        assertEquals(3, users.getTotalPages());
    }
    @Test
    public void shouldFetchAverageMovieRateCorrectly(){
        when(userRepository.findAverageMovieRate(anyLong())).thenReturn(5.0);

        Double rate = userService.fetchAverageRate(1L);

        assertEquals(5.0, rate, 0.01);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFetchAverageMovieRateAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> userService.fetchAverageRate(null));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchMostRatedCategoryCorrectly(){
        when(userRepository.findUserMovieRates(anyLong())).thenReturn(List.of(MovieCategory.COMEDY));

        MovieCategory category = userService.fetchMostRatedCategory(1L);

        assertEquals(MovieCategory.COMEDY, category);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFetchMostRatedCategoryAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> userService.fetchMostRatedCategory(null));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldReturnNullWhenFindMostRatedCategoryAndFoundNoCategory(){
        when(userRepository.findUserMovieRates(anyLong())).thenReturn(Collections.emptyList());

        MovieCategory category = userService.fetchMostRatedCategory(1L);

        assertNull(category);
    }
    @Test
    public void shouldFetchHighestRatedMoviesByUser(){
        when(userRepository.findTopRatedMovies(anyLong())).thenReturn(List.of(movieRate));

        List<MovieDtoWithUserRate> movies = userService.fetchHighestRatedMoviesByUser(1L);

        assertEquals(1, movies.size());
        assertEquals("Movie", movies.getFirst().getText());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchHighestRatedMoviesByUserAndNoneFound(){
        when(userRepository.findTopRatedMovies(anyLong())).thenReturn(Collections.emptyList());

        List<MovieDtoWithUserRate> movies = userService.fetchHighestRatedMoviesByUser(1L);

        assertEquals(0, movies.size());
    }
    @Test
    public void shouldFetchHighestRatedActorsByUser(){
        when(userRepository.findTopRatedActors(anyLong())).thenReturn(List.of(actorRate));

        List<ActorDtoWithUserRate> fetchedActors = userService.fetchHighestRatedActorsByUser(1L);

        assertEquals(1, fetchedActors.size());
        assertEquals("Alex Smith", fetchedActors.getFirst().getText());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchHighestRatedActorsByUserAndNoneFound(){
        when(userRepository.findTopRatedActors(anyLong())).thenReturn(Collections.emptyList());

        List<ActorDtoWithUserRate> fetchedActors = userService.fetchHighestRatedActorsByUser(1L);

        assertEquals(0, fetchedActors.size());
    }
    @Test
    public void shouldFetchHighestRatedDirectorsByUser(){
        when(userRepository.findTopRatedDirectors(anyLong())).thenReturn(List.of(directorRate));

        List<DirectorDtoWithUserRate> directors = userService.fetchHighestRatedDirectorsByUser(1L);

        assertEquals(1, directors.size());
        assertEquals("John Smith", directors.getFirst().getText());
    }
    @Test
    public void shouldReturnEmptyListWhenFetchHighestRatedDirectorsByUserAndNoneFound(){
        when(userRepository.findTopRatedDirectors(anyLong())).thenReturn(Collections.emptyList());

        List<DirectorDtoWithUserRate> directors = userService.fetchHighestRatedDirectorsByUser(1L);

        assertEquals(0, directors.size());
    }
    @Test
    public void shouldFetchMovieRateCountCorrectly(){
        when(userRepository.findMovieRateCount(anyLong())).thenReturn(999L);

        Long count = userService.fetchMovieRateCount(1L);

        assertEquals(999L, count);
    }
    @Test
    public void shouldReturn0WhenFetchMovieRateCountAndNoRatesExists(){
        when(userRepository.findMovieRateCount(anyLong())).thenReturn(0L);

        Long count = userService.fetchMovieRateCount(1L);

        assertEquals(0L, count);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFetchMovieRateCountAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> userService.fetchMovieRateCount(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchActorRateCountCorrectly(){
        when(userRepository.findActorRateCount(anyLong())).thenReturn(999L);

        Long count = userService.fetchActorRateCount(1L);

        assertEquals(999L, count);
    }
    @Test
    public void shouldReturn0WhenFetchActorRateCountAndNoRatesExists(){
        when(userRepository.findActorRateCount(anyLong())).thenReturn(0L);

        Long count = userService.fetchActorRateCount(1L);

        assertEquals(0L, count);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFetchActorRateCountAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> userService.fetchActorRateCount(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchDirectorRateCountCorrectly(){
        when(userRepository.findDirectorRateCount(anyLong())).thenReturn(999L);

        Long count = userService.fetchDirectorRateCount(1L);

        assertEquals(999L, count);
    }
    @Test
    public void shouldReturn0WhenFetchDirectorRateCountAndNoRatesExists(){
        when(userRepository.findDirectorRateCount(anyLong())).thenReturn(0L);

        Long count = userService.fetchDirectorRateCount(1L);

        assertEquals(0L, count);
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenFetchDirectorRateCountAndInvalidId(){
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> userService.fetchDirectorRateCount(-1L));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldFetchAllRatedMoviesCorrectly(){
        List<MovieRate> movieRates = new ArrayList<>();
        movieRates.add(movieRate);
        when(userRepository.findAllRatedMovies(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(movieRates, pageable, 1));

        Page<MovieDtoWithUserRate> ratedMovies = userService.fetchAllRatedMovies(1L, 0, 1);

        assertEquals(1, ratedMovies.getTotalPages());
        assertEquals(1, ratedMovies.getTotalElements());
    }
    @Test
    public void shouldFetchAllRatedMoviesCorrectlyWhenNpMoviesFound(){
        when(userRepository.findAllRatedMovies(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        Page<MovieDtoWithUserRate> ratedMovies = userService.fetchAllRatedMovies(1L, 0, 1);

        assertEquals(0, ratedMovies.getTotalElements());
    }
    @Test
    public void shouldFetchAllRatedActorsCorrectly(){
        List<ActorRate> actorRates = new ArrayList<>();
        actorRates.add(actorRate);
        when(userRepository.findAllRatedActors(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(actorRates, pageable, 1));

        Page<ActorDtoWithUserRate> ratedActors = userService.fetchAllRatedActors(1L, 0, 1);

        assertEquals(1, ratedActors.getTotalPages());
        assertEquals(1, ratedActors.getTotalElements());
    }
    @Test
    public void shouldFetchAllRatedActorsCorrectlyWhenNoMoviesFound(){
        when(userRepository.findAllRatedActors(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        Page<ActorDtoWithUserRate> ratedActors = userService.fetchAllRatedActors(1L, 0, 1);

        assertEquals(0, ratedActors.getTotalElements());
    }
    @Test
    public void shouldFetchAllRatedDirectorsCorrectly(){
        List<DirectorRate> directorRates = new ArrayList<>();
        directorRates.add(directorRate);
        when(userRepository.findAllRatedDirectors(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(directorRates, pageable, 1));

        Page<DirectorDtoWithUserRate> ratedDirectors = userService.fetchAllRatedDirectors(1L, 0, 1);

        assertEquals(1, ratedDirectors.getTotalPages());
        assertEquals(1, ratedDirectors.getTotalElements());
    }
    @Test
    public void shouldFetchAllRatedDirectorsCorrectlyWhenNoMoviesFound(){
        when(userRepository.findAllRatedDirectors(anyLong(), any(Pageable.class))).thenReturn(Page.empty());

        Page<DirectorDtoWithUserRate> ratedMovies = userService.fetchAllRatedDirectors(1L, 0, 1);

        assertEquals(0, ratedMovies.getTotalElements());
    }
    @Test
    public void shouldLoadUserEntityByIdCorrectly(){
        when(userRepository.findByIdWithComments(anyLong())).thenReturn(Optional.of(user));

        UserDataDto loadedUser = userService.loadUserEntityById(1L);

        assertEquals("Username", loadedUser.getUsername());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenLoadUserEntityByIdAndNoUserFound(){
        when(userRepository.findByIdWithComments(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> userService.loadUserEntityById(1L));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldLoadUserEntityByIdVaadinCorrectly(){
        when(userRepository.findByIdWithComments(anyLong())).thenReturn(Optional.of(user));

        UserEntity loadedUser = userService.loadUserEntityByIdVaadin(1L);

        assertEquals("Username", loadedUser.getUsername());
    }
    @Test
    public void shouldReturnNullWhenLoadUserEntityByIdVaadinAndNoUserFound(){
        when(userRepository.findByIdWithComments(anyLong())).thenReturn(Optional.empty());

        UserEntity loadedUser = userService.loadUserEntityByIdVaadin(1L);

        assertNull(loadedUser);
    }
    @Test
    public void shouldLoadUserEntityByEmailCorrectly(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserEntity loadedUser = userService.loadUserEntityByEmail("email");

        assertEquals("Username", loadedUser.getUsername());
    }
    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenLoadUserEntityByEmailAndNoUserFound(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserEntityByEmail("email"));
        assertEquals("Account not found", ex.getMessage());
    }
    @Test
    public void shouldGetUsernameCorrectly(){
        when(userRepository.findByEmailWithRoles(anyString())).thenReturn(Optional.of(user));

        String email = userService.getUsername("email");

        assertEquals("Username", email);
    }
    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenGetUsernameAndUserNotFound(){
        when(userRepository.findByEmailWithRoles(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> userService.getUsername("email"));
        assertTrue(ex.getMessage().contains("Account with email"));
    }

    private Page<UserEntity> setUpUserList(){
        List<UserEntity> users = new ArrayList<>();
        users.add(user);
        users.add(UserEntity.builder()
                .userId(1L)
                .username("User1")
                .email("user1@usercom")
                .banned(false)
                .banExpiration(null)
                .roles(List.of(role))
                .build());
        users.add(UserEntity.builder()
                .userId(2L)
                .username("User2")
                .email("user2@usercom")
                .banned(false)
                .banExpiration(null)
                .roles(List.of(role))
                .build());
        users.add(UserEntity.builder()
                .userId(3L)
                .username("User3")
                .email("user3@usercom")
                .banned(false)
                .banExpiration(null)
                .roles(List.of(role))
                .build());
        users.add(UserEntity.builder()
                .userId(4L)
                .username("User4")
                .email("user4@usercom")
                .banned(false)
                .banExpiration(null)
                .roles(List.of(role))
                .build());
        users.add(UserEntity.builder()
                .userId(5L)
                .username("User5")
                .email("user5@usercom")
                .banned(false)
                .banExpiration(null)
                .roles(List.of(role))
                .build());
        return new PageImpl<>(users, pageable, 6);
    }









}

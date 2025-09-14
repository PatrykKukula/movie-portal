package pl.patrykkukula.MovieReviewPortal.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.BanDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserDataDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDataDto> findUserById(@PathVariable(value = "userId") Long userId){
        return ResponseEntity.ok(userService.loadUserEntityById(userId));
    }
    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BanDto banDto){
        return userService.banUser(banDto) ? ResponseEntity.ok("User banned successfully") :
                ResponseEntity.badRequest().body("Failed to ban user. Please try again");
    }
    @PostMapping("/remove-ban")
    public ResponseEntity<String> removeBan(@RequestParam String username){
        return userService.removeBan(username) ? ResponseEntity.ok("Removed ban from user successfully") :
                ResponseEntity.badRequest().body("Failed to remove ban. Please try again");
    }
    @PostMapping("/add-role")
    public ResponseEntity<String> addRoleToUser(@RequestParam String username, @RequestParam String role){
        return userService.addRole(username, role) ? ResponseEntity.ok("Role %s added successfully".formatted(role)) :
                ResponseEntity.badRequest().body("Failed to add role. Please try again");
    }
    @DeleteMapping("/remove-role")
    public ResponseEntity<String> removeRoleFromUser(@RequestParam String username, @RequestParam String role){
        return userService.removeRole(username, role) ? ResponseEntity.ok("Role %s removed successfully".formatted(role)) :
                ResponseEntity.badRequest().body("Failed to remove role. Please try again");
    }
    @GetMapping("/count")
    public ResponseEntity<Integer> countRegisteredUsers(){
        return ResponseEntity.ok(userService.countUsers(null));
    }
    @GetMapping("/find-all")
    public ResponseEntity<Page<UserDataDto>> findAllUsers(@RequestParam int pageNo, @RequestParam int pageSize){
        return ResponseEntity.ok(userService.fetchAllUsers(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "username"), null));
    }
    @GetMapping("/average-movie-rate")
    public ResponseEntity<Double> findAverageMovieRateForUserById(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchAverageRate(userId));
    }
    @GetMapping("/most-rated-category")
    public ResponseEntity<MovieCategory> findMostRatedMovieCategoryByUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchMostRatedCategory(userId));
    }
    // fix null
    @GetMapping("/highest-rated-movies")
    public ResponseEntity<List<MovieDtoWithUserRate>> findHighestRatedMoviesForUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchHighestRatedMoviesByUser(userId));
    }
    @GetMapping("/highest-rated-actors")
    public ResponseEntity<List<ActorDtoWithUserRate>> findHighestRatedActorsForUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchHighestRatedActorsByUser(userId));
    }
    @GetMapping("/highest-rated-directors")
    public ResponseEntity<List<DirectorDtoWithUserRate>> findHighestRatedDirectorsForUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchHighestRatedDirectorsByUser(userId));
    }
    @GetMapping("/rated-movies-count")
    public ResponseEntity<Long> countMoviesRatedByUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchMovieRateCount(userId));
    }
    @GetMapping("/rated-actors-count")
    public ResponseEntity<Long> countActorsRatedByUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchActorRateCount(userId));
    }
    @GetMapping("/rated-directors-count")
    public ResponseEntity<Long> countDirectorsRatedByUser(@RequestParam Long userId){
        return ResponseEntity.ok(userService.fetchDirectorRateCount(userId));
    }
    @GetMapping("/rated-movies")
    public ResponseEntity<Page<MovieDtoWithUserRate>> findAllMoviesRatedByUser(@RequestParam Long userId,
                                                                               @RequestParam(required = false, defaultValue = "0") Integer pageNo,
                                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(userService.fetchAllRatedMovies(userId, pageNo, pageSize));
    }
    @GetMapping("/rated-actors")
    public ResponseEntity<Page<ActorDtoWithUserRate>> findAllActorsRatedByUser(@RequestParam Long userId,
                                                                               @RequestParam(required = false, defaultValue = "0") Integer pageNo,
                                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(userService.fetchAllRatedActors(userId, pageNo, pageSize));
    }
    @GetMapping("/rated-directors")
    public ResponseEntity<Page<DirectorDtoWithUserRate>> findAllDirectorsRatedByUser(@RequestParam Long userId,
                                                                               @RequestParam(required = false, defaultValue = "0") Integer pageNo,
                                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(userService.fetchAllRatedDirectors(userId, pageNo, pageSize));
    }
}

package pl.patrykkukula.MovieReviewPortal.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieRate.MovieRateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ErrorResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieServiceImpl movieService;

    @PostMapping
    public ResponseEntity<ResponseDto> addMovie(@Valid @RequestBody MovieDto movieDto, HttpServletRequest request) {
        Long movieId = movieService.addMovie(movieDto);
        URI location = setUri(movieId, request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{movieId}")
    public ResponseEntity<ResponseDto> deleteMovie(@PathVariable Long movieId) {
         movieService.deleteMovie(movieId);
         return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @PostMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity<?> addActorToMovie(@PathVariable Long movieId, @PathVariable Long actorId, HttpServletRequest request) {
        boolean isAdded = movieService.addActorToMovie(movieId, actorId);

        return isAdded ? ResponseEntity.ok(new ResponseDto(STATUS_200, STATUS_200_MESSAGE)) :
                         ResponseEntity.badRequest().
                                 body(new ErrorResponseDto(request.getRequestURI(),STATUS_400, STATUS_400_MESSAGE, "Actor already added to movie", LocalDateTime.now()));
    }
    @DeleteMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity<?> removeActorFromMovie(@PathVariable Long movieId, @PathVariable Long actorId, HttpServletRequest request) {
        boolean isRemoved = movieService.removeActorFromMovie(movieId, actorId);

        return isRemoved ?
                ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE)) :
               new ResponseEntity<>(new ErrorResponseDto(request.getRequestURI(),STATUS_404, STATUS_404_MESSAGE, "Actor is not added to this movie", LocalDateTime.now()),HttpStatus.NOT_FOUND);
    }
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDtoWithDetails> fetchMovieById(@PathVariable Long movieId) {
        return ResponseEntity.ok().body(movieService.fetchMovieDetailsById(movieId));
    }
    @GetMapping("/search")
    public ResponseEntity<List<MovieDtoBasic>> fetchAllMoviesByTitle(@RequestParam(name = "title") String title,
                                                                     @RequestParam(name = "sorted", required = false, defaultValue = "ASC") String sorted) {
        return ResponseEntity.ok(movieService.fetchAllMoviesByTitle(title, sorted));
    }
    @GetMapping
    public ResponseEntity<List<MovieDtoBasic>> fetchAllMovies(@RequestParam(name = "sorted", required = false, defaultValue = "ASC") String sorted) {
        return ResponseEntity.ok(movieService.fetchAllMovies(sorted));
    }
    @PatchMapping("/{movieId}")
    public ResponseEntity<ResponseDto> updateMovie(@PathVariable Long movieId, @Valid @RequestBody MovieUpdateDto movieDto) {
        movieService.updateMovie(movieId, movieDto);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @PostMapping("/rate")
    public ResponseEntity<ResponseDto> addRateToMovie(@Valid @RequestBody MovieRateDto movieRateDto){
        movieService.addRateToMovie(movieRateDto);
        return ResponseEntity.ok(new ResponseDto(STATUS_200, STATUS_200_MESSAGE));
    }
    @DeleteMapping("/{movieId}/rate")
    public ResponseEntity<?> removeRateFromMovie(@PathVariable Long movieId, WebRequest request){
        boolean isRemoved = movieService.removeRate(movieId);
        return isRemoved ? ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE)) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
                        request.getDescription(false),
                        STATUS_404,
                        STATUS_404_MESSAGE,
                        "You didn't set rate for this movie",
                        LocalDateTime.now()
                        ));
    }
}

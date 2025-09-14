package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Service.IActorService;

import java.net.URI;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@Slf4j
@RestController
@RequestMapping(value = "/api/actors", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ActorController {

    private final IActorService actorService;

    @PostMapping
    public ResponseEntity<ResponseDto> addActor(@Valid @RequestBody ActorDto actorDto, HttpServletRequest request) {
        log.info("Invoking add actor controller");
        Long actorId = actorService.addActor(actorDto);
        URI location = setUri(actorId, request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteActor(@PathVariable Long id) {
        actorService.removeActor(id);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ActorDtoWithMovies> getActorByIdWithMovies(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.fetchActorByIdWithMovies(id));
    }
    @GetMapping
    public ResponseEntity<List<ActorDto>> getAllActors(@RequestParam(name = "sorted", required = false) String sorted,
                                                        @RequestParam(name = "findBy", required = false) String findBy) {
        if (findBy == null || findBy.isEmpty()) {
            return ResponseEntity.ok(actorService.fetchAllActors(sorted));
        }
        return ResponseEntity.ok(actorService.fetchAllActorsByNameOrLastName(findBy, sorted));
    }
    @GetMapping("/top-rated")
    public ResponseEntity<List<EntityWithRate>> getTopRatedActors(){
        return ResponseEntity.ok(actorService.fetchTopRatedActors());
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> updateActor(@PathVariable Long id, @Valid @RequestBody ActorUpdateDto actorUpdateDto) {
        actorService.updateActor(actorUpdateDto, id);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @PostMapping("/rate/add")
    public ResponseEntity<RatingResult> addRateToActor(@Valid @RequestBody RateDto rateDto){
        return ResponseEntity.accepted().body(actorService.addRateToActor(rateDto));
    }
    @DeleteMapping("/rate/remove/{id}")
    public ResponseEntity<ResponseDto> removeRateFromActor(@PathVariable Long id){
        actorService.removeActor(id);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

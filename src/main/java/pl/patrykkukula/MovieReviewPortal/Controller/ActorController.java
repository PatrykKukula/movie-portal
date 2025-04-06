package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Service.IActorService;

import java.net.URI;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@RestController
@RequestMapping(value = "/actors", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ActorController {

    private IActorService actorService;

    @PostMapping
    public ResponseEntity<ResponseDto> addActor(@Valid @RequestBody ActorDto actorDto, HttpServletRequest request) {
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
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> updateActor(@PathVariable Long id, @Valid @RequestBody ActorUpdateDto actorUpdateDto) {
        actorService.updateActor(actorUpdateDto, id);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

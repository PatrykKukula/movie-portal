package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Service.IDirectorService;

import java.net.URI;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@RestController
@RequestMapping(value = "/api/directors", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class DirectorController {

    private final IDirectorService directorService;

    @PostMapping
    public ResponseEntity<ResponseDto> addDirector(@Valid @RequestBody DirectorDto directorDto, HttpServletRequest request) {
        Long directorId = directorService.addDirector(directorDto);
        URI location = setUri(directorId,request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteDirector(@PathVariable Long id) {
        directorService.removeDirector(id);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DirectorDtoWithMovies> getDirectorByIdWithMovies(@PathVariable Long id) {
        return ResponseEntity.ok().body(directorService.fetchDirectorByIdWithMovies(id));
    }
    @GetMapping
    public ResponseEntity<List<DirectorDto>> getAllDirectors(@RequestParam(name = "sorted", required = false, defaultValue = "ASC") String sorted,
    @RequestParam(name = "findBy", required = false) String findBy) {
        if (findBy == null || findBy.isEmpty()) {
            return ResponseEntity.ok().body(directorService.fetchAllDirectors(sorted));
        }
        return ResponseEntity.ok().body(directorService.fetchAllDirectorsByNameOrLastName(findBy, sorted));
    }
    @PatchMapping("/edit/{id}")
    public ResponseEntity<ResponseDto> updateDirector(@PathVariable Long id, @Valid @RequestBody DirectorUpdateDto directorDto) {
        directorService.updateDirector(directorDto,id);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

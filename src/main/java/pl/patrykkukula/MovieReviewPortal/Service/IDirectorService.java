package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;

import java.util.List;

public interface IDirectorService {
    /*
        COMMON SECTION
     */
    Long addDirector(DirectorDto directorDto);
    void removeDirector(Long directorId);
    DirectorDtoWithMovies fetchDirectorByIdWithMovies(Long directorId);
    List<DirectorDto> fetchAllDirectors(String sorted);
    List<DirectorDto> fetchAllDirectorsByNameOrLastName(String name, String sorted);
    /*
        REST API SECTION
     */
    void updateDirector(DirectorUpdateDto directorDto, Long id);
    /*
        VAADIN VIEW SECTION
     */
    DirectorDto fetchDirectorById(Long id);
    DirectorSummaryDto fetchDirectorSummaryById(Long id);
    List<DirectorSummaryDto> fetchAllDirectorsSummary();
    void updateDirectorVaadin(Long id, DirectorDto directorDto);
}

package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorUpdateDto;

import java.util.List;

public interface IDirectorService {

    Long addDirector(DirectorDto directorDto);
    void removeDirector(Long directorId);
    DirectorDtoWithMovies fetchDirectorByIdWithMovies(Long directorId);
    List<DirectorDto> fetchAllDirectors(String sorted);
    List<DirectorSummaryDto> fetchAllDirectorsSummary();
    List<DirectorDto> fetchAllDirectorsByNameOrLastName(String name, String sorted);
    void updateDirector(DirectorUpdateDto directorDto, Long id);
}

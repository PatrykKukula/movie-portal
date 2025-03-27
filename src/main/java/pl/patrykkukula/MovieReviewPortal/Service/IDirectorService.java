package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.DirectorUpdateDto;

import java.util.List;

public interface IDirectorService {

    Long addDirector(DirectorDto directorDto);
    void removeDirector(Long directorId);
    DirectorDtoWithMovies fetchDirectorByIdWithMovies(Long directorId);
    List<DirectorDto> fetchAllDirectors(String sorted);
    List<DirectorDto> fetchAllDirectorsByNameOrLastName(String name, String sorted);
    void updateDirector(DirectorUpdateDto directorDto, Long id);
}

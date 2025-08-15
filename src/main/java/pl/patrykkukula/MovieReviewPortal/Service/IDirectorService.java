package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Director.*;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;

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
    RatingResult addRateToDirector(RateDto rateDto);
    Double removeRate(Long directorId);
    /*
        REST API SECTION
     */
    void updateDirector(DirectorUpdateDto directorDto, Long id);
    /*
        VAADIN VIEW SECTION
     */
    List<DirectorSummaryDto> fetchAllDirectorsSummary();
    List<DirectorViewDto> fetchAllDirectorsView(String searchedText, String sorting);
    DirectorDto fetchDirectorById(Long id);
    DirectorSummaryDto fetchDirectorSummaryById(Long id);
    void updateDirectorVaadin(Long id, DirectorDto directorDto);
    RateDto fetchRateByDirectorIdAndUserId(Long directorId, Long userId);
    List<EntityWithRate> fetchTopRatedDirectors();
}

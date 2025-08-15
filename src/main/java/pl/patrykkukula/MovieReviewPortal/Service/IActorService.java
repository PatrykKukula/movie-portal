package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Actor.*;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;

import java.util.List;

public interface IActorService {
    /*
        COMMON SECTION
     */
    Long addActor(ActorDto actorDto);
    void removeActor(Long actorId);
    ActorDtoWithMovies fetchActorByIdWithMovies(Long actorId);
    List<ActorDto> fetchAllActors(String sorted);
    List<ActorDto> fetchAllActorsByNameOrLastName(String name, String sorted);
    RatingResult addRateToActor(RateDto rateDto);
    Double removeRate(Long movieId);
    /*
        REST API SECTION
     */
    void updateActor(ActorUpdateDto actorUpdateDto, Long id);
    /*
        VAADIN VIEW SECTION
     */
    List<ActorSummaryDto> fetchAllActorsSummary();
    List<ActorViewDto> fetchAllActorsView(String searchedText, String sorting);
    List<ActorSummaryDto> fetchAllActorsSummaryByIds(List<Long> actorIds);
    ActorDto fetchActorById(Long id);
    void updateActorVaadin(Long id, ActorDto actorDto);
    RateDto fetchRateByActorIdAndUserId(Long actorId, Long userId);
    List<EntityWithRate> fetchTopRatedActors();
}

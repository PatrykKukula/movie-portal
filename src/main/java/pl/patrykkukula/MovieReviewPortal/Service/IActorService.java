package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;

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
    /*
        REST API SECTION
     */
    void updateActor(ActorUpdateDto actorUpdateDto, Long id);
    /*
        VAADIN VIEW SECTION
     */
    List<ActorSummaryDto> fetchAllActorsSummary();
    List<ActorSummaryDto> fetchAllActorsSummaryByIds(List<Long> actorIds);
    ActorDto fetchActorById(Long id);
    void updateActorVaadin(Long id, ActorDto actorDto);
}

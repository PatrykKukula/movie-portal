package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;

import java.util.List;

public interface IActorService {
    Long addActor(ActorDto actorDto);
    void removeActor(Long actorId);
    ActorDtoWithMovies fetchActorByIdWithMovies(Long actorId);
    List<ActorDto> fetchAllActors(String sorted);
    List<ActorSummaryDto> fetchAllActorsSummary();
    List<ActorDto> fetchAllActorsByNameOrLastName(String name, String sorted);
    void updateActor(ActorUpdateDto actorUpdateDto, Long id);
}

package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.ActorUpdateDto;

import java.util.List;

public interface IActorService {
    Long addActor(ActorDto actorDto);
    void removeActor(Long actorId);
    ActorDtoWithMovies fetchActorByIdWithMovies(Long actorId);
    List<ActorDto> fetchAllActors(String sorted);
    List<ActorDto> fetchAllActorsByNameOrLastName(String name, String sorted);
    void updateActor(ActorUpdateDto actorUpdateDto, Long id);
}

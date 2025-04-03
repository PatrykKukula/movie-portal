package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

public class ActorMapper {

    public static Actor mapToActor(ActorDto actorDto) {
        return Actor.builder()
                        .firstName(actorDto.getFirstName())
                        .lastName(actorDto.getLastName())
                        .country(actorDto.getCountry())
                        .dateOfBirth(actorDto.getDateOfBirth())
                        .build();
    }
    public static ActorDto mapToActorDto(Actor actor) {
        return ActorDto.builder()
                        .id(actor.getActorId())
                        .firstName(actor.getFirstName())
                        .lastName(actor.getLastName())
                        .country(actor.getCountry())
                        .dateOfBirth(actor.getDateOfBirth())
                        .build();
    }
    public static ActorDtoWithMovies mapToActorDtoWithMovies(Actor actor){
        List<MovieDto> moviesDto = Optional.ofNullable(actor.getMovies())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(MovieMapper::mapToMovieDto)
                    .toList();

        return ActorDtoWithMovies.builder()
                .id(actor.getActorId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .country(actor.getCountry())
                .dateOfBirth(actor.getDateOfBirth())
                .movies(moviesDto)
                .build();
    }
    public static Actor mapToActorUpdate(ActorUpdateDto actorUpdateDto, Actor actor) {
        updateField(actorUpdateDto::getFirstName, actor::setFirstName);
        updateField(actorUpdateDto::getLastName, actor::setLastName);
        updateField(actorUpdateDto::getCountry, actor::setCountry);
        updateField(actorUpdateDto::getDateOfBirth, actor::setDateOfBirth);
        return actor;
    }
}

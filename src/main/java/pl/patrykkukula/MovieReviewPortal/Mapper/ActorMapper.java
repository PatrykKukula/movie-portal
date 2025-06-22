package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateDateField;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

public class ActorMapper {

    public static Actor mapToActor(ActorDto actorDto) {
        return Actor.builder()
                        .firstName(actorDto.getFirstName())
                        .lastName(actorDto.getLastName())
                        .country(actorDto.getCountry())
                        .dateOfBirth(actorDto.getDateOfBirth())
                        .biography(actorDto.getBiography())
                        .build();
    }
    public static ActorDto mapToActorDto(Actor actor) {
        return ActorDto.builder()
                        .id(actor.getActorId())
                        .firstName(actor.getFirstName())
                        .lastName(actor.getLastName())
                        .country(actor.getCountry())
                        .dateOfBirth(actor.getDateOfBirth())
                        .biography(actor.getBiography())
                        .build();
    }
    public static ActorDtoWithMovies mapToActorDtoWithMovies(Actor actor){
        List<MovieDtoBasic> moviesDto = Optional.ofNullable(actor.getMovies())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(MovieMapper::mapToMovieDtoBasic)
                    .toList();

        return ActorDtoWithMovies.builder()
                .id(actor.getActorId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .country(actor.getCountry())
                .dateOfBirth(actor.getDateOfBirth())
                .biography(actor.getBiography())
                .movies(moviesDto)
                .build();
    }
    public static Actor mapToActorUpdate(ActorUpdateDto actorUpdateDto, Actor actor) {
        updateField(actorUpdateDto::getFirstName, actor::setFirstName);
        updateField(actorUpdateDto::getLastName, actor::setLastName);
        updateField(actorUpdateDto::getCountry, actor::setCountry);
        updateDateField(actorUpdateDto::getDateOfBirth, actor::setDateOfBirth);
        updateField(actorUpdateDto::getBiography, actor::setBiography);
        return actor;
    }
    public static ActorSummaryDto mapToActorSummary(Actor actor){
        return ActorSummaryDto.builder()
                .id(actor.getActorId())
                .fullName(actor.getFirstName() + " " + actor.getLastName())
                .build();
    }

}

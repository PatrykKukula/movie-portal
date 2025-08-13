package pl.patrykkukula.MovieReviewPortal.Mapper;

import pl.patrykkukula.MovieReviewPortal.Dto.Actor.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.ActorRate;
import pl.patrykkukula.MovieReviewPortal.Model.MovieRate;

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
    public static ActorViewDto mapToActorViewDto(Actor actor) {
        return ActorViewDto.builder()
                .id(actor.getActorId())
                .firstName(actor.getFirstName())
                .lastName(actor.getLastName())
                .country(actor.getCountry())
                .dateOfBirth(actor.getDateOfBirth())
                .averageRate(actor.averageActorRate())
                .rateNumber(actor.getActorRates().size())
                .build();
    }
    public static ActorDtoWithMovies mapToActorDtoWithMovies(Actor actor, Double rate, Integer rateNumber){
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
                .rating(rate)
                .rateNumber(rateNumber)
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
    public static Actor mapToActorUpdateVaadin(ActorDto actorDto, Actor actor) {
        actor.setFirstName(actorDto.getFirstName());
        actor.setLastName(actorDto.getLastName());
        actor.setCountry(actorDto.getCountry());
        actor.setDateOfBirth(actorDto.getDateOfBirth());
        actor.setBiography(actorDto.getBiography());
        return actor;
    }
    public static ActorDtoWithUserRate mapToActorDtoWithUserRate(ActorRate actorRate){
        return ActorDtoWithUserRate.builder()
                .id(actorRate.getActor().getActorId())
                .firstName(actorRate.getActor().getFirstName())
                .lastName(actorRate.getActor().getLastName())
                .userRate(actorRate.getRate())
                .build();
    }

}

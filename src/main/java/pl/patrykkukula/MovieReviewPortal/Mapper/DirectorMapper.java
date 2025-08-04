package pl.patrykkukula.MovieReviewPortal.Mapper;

import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorViewDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.Director;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateDateField;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

@Slf4j
public class DirectorMapper {

    public static Director mapToDirector(DirectorDto directorDto) {
        return Director.builder()
                .firstName(directorDto.getFirstName())
                .lastName(directorDto.getLastName())
                .country(directorDto.getCountry())
                .biography(directorDto.getBiography())
                .dateOfBirth(directorDto.getDateOfBirth())
                .build();
    }
    public static DirectorDto mapToDirectorDto(Director director) {
        return DirectorDto.builder()
                .id(director.getDirectorId())
                .firstName(director.getFirstName())
                .lastName(director.getLastName())
                .country(director.getCountry())
                .biography(director.getBiography())
                .dateOfBirth(director.getDateOfBirth())
                .build();
    }
    public static DirectorViewDto mapToDirectorViewDto(Director director, Double rate) {
        return DirectorViewDto.builder()
                .id(director.getDirectorId())
                .firstName(director.getFirstName())
                .lastName(director.getLastName())
                .country(director.getCountry())
                .dateOfBirth(director.getDateOfBirth())
                .biography(director.getBiography())
                .rate(rate)
                .build();
    }
    public static DirectorDtoWithMovies mapToDirectorDtoWithMovies(Director director, Double rate, Integer rateNumber) {
        List<MovieDtoBasic> moviesDto = Optional.ofNullable(director.getMovies())
                .orElse(Collections.emptyList())
                .stream()
                .map(MovieMapper::mapToMovieDtoBasic)
                .toList();

        return DirectorDtoWithMovies.builder()
                .id(director.getDirectorId())
                .firstName(director.getFirstName())
                .lastName(director.getLastName())
                .country(director.getCountry())
                .biography(director.getBiography())
                .dateOfBirth(director.getDateOfBirth())
                .rating(rate)
                .rateNumber(rateNumber)
                .movies(moviesDto)
                .build();
    }
    public static Director mapToDirectorUpdate(DirectorUpdateDto directorDto, Director director) {
        updateField(directorDto::getFirstName, director::setFirstName);
        updateField(directorDto::getLastName, director::setLastName);
        updateField(directorDto::getCountry, director::setCountry);
        updateField(directorDto::getBiography, director::setBiography);
        updateDateField(directorDto::getDateOfBirth, director::setDateOfBirth);
        return director;
    }
    public static Director mapToDirectorUpdateVaadin(DirectorDto directorDto, Director director) {
        director.setFirstName(directorDto.getFirstName());
        director.setLastName(directorDto.getLastName());
        director.setCountry(directorDto.getCountry());
        director.setBiography(directorDto.getBiography());
        director.setDateOfBirth(directorDto.getDateOfBirth());
        return director;
    }
    public static DirectorSummaryDto mapToDirectorSummary(Director director) {
        if (director != null) {
            return DirectorSummaryDto.builder()
                    .id(director.getDirectorId())
                    .fullName(director.getFirstName() + " " + director.getLastName())
                    .build();
        }
        return new DirectorSummaryDto();
    }
}

package pl.patrykkukula.MovieReviewPortal.Mapper;
import pl.patrykkukula.MovieReviewPortal.Dto.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.DirectorUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

public class DirectorMapper {

    public static Director mapToDirector(DirectorDto directorDto) {
        return Director.builder()
                        .firstName(directorDto.getFirstName())
                        .lastName(directorDto.getLastName())
                        .country(directorDto.getCountry())
                        .dateOfBirth(directorDto.getDateOfBirth())
                        .build();
    }
    public static DirectorDto mapToDirectorDto (Director director) {
        return DirectorDto.builder()
                .firstName(director.getFirstName())
                .lastName(director.getLastName())
                .country(director.getCountry())
                .dateOfBirth(director.getDateOfBirth())
                .build();
    }
    public static DirectorDtoWithMovies mapToDirectorDtoWithMovies (Director director) {
        List<MovieDto> moviesDto = Optional.ofNullable(director.getMovies())
                .orElse(Collections.emptyList())
                .stream()
                .map(MovieMapper::mapToMovieDto)
                .toList();

        return DirectorDtoWithMovies.builder()
                .firstName(director.getFirstName())
                .lastName(director.getLastName())
                .country(director.getCountry())
                .dateOfBirth(director.getDateOfBirth())
                .movies(moviesDto)
                .build();
    }
        public static Director mapToDirectorUpdate (DirectorUpdateDto directorDto, Director director) {
            updateField(directorDto::getFirstName, director::setFirstName);
            updateField(directorDto::getLastName, director::setLastName);
            updateField(directorDto::getCountry, director::setCountry);
            updateField(directorDto::getDateOfBirth, director::setDateOfBirth);
            return director;
        }
}

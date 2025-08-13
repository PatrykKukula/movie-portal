package pl.patrykkukula.MovieReviewPortal.Mapper;

import lombok.Data;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.*;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Model.MovieRate;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateDateField;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

@Data
public class MovieMapper {

    public static Movie mapToMovie(MovieDto movieDto){
        String title = movieDto.getTitle().substring(0,1).toUpperCase() + movieDto.getTitle().substring(1);
        return Movie.builder()
                .title(title)
                .description(movieDto.getDescription())
                .category(movieDto.getCategory())
                .releaseDate(movieDto.getReleaseDate())
                .build();
    }
    public static MovieDtoWithDetails mapToMovieDtoWithDetails(Movie movie, Double rate, Integer rateNumber){
        Director director = movie.getDirector();
        DirectorDto directorDto = new DirectorDto();
        if (director != null) {
            directorDto = pl.patrykkukula.MovieReviewPortal.Mapper.DirectorMapper.mapToDirectorDto(director);
        }

        return MovieDtoWithDetails.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .rating(rate)
                .rateNumber(rateNumber)
                .releaseDate(movie.getReleaseDate())
                .category(movie.getCategory().toString())
                .description(movie.getDescription())
                .director(directorDto)
                .actors(
                        movie.getActors().stream()
                                .map(ActorMapper::mapToActorDto)
                                .toList()
                )
                .build();
    }
    public static MovieDto mapToMovieDto(Movie movie){
        MovieDto dto = MovieDto.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .category(movie.getCategory())
                .releaseDate(movie.getReleaseDate())
                .actorIds(
                        movie.getActors().stream().mapToLong(Actor::getActorId).boxed().toList())
                .build();
        Director director = movie.getDirector();
        if (director != null) {
            dto.setDirectorId(director.getDirectorId());
        }
        return dto;
    }
    public static MovieViewDto mapToMovieViewDto(Movie movie){
        return MovieViewDto.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .category(movie.getCategory())
                .releaseDate(movie.getReleaseDate())
                .rateNumber(movie.movieRatesNumber())
                .averageRate(movie.averageMovieRate())
                .build();
    }
    public static Movie mapMovieDtoToMovie(MovieDto movieDto, Movie movie){
        updateField(movieDto::getTitle, movie::setTitle);
        updateField(movieDto::getDescription, movie::setDescription);
        updateDateField(movieDto::getReleaseDate, movie::setReleaseDate);
        return movie;
    }
    public static Movie mapMovieUpdateDtoToMovieUpdate(MovieUpdateDto movieDto, Movie movie){
        updateField(movieDto::getTitle, movie::setTitle);
        updateField(movieDto::getDescription, movie::setDescription);
        updateDateField(movieDto::getReleaseDate, movie::setReleaseDate);
        return movie;
    }
    public static MovieDtoBasic mapToMovieDtoBasic(Movie movie){
        return MovieDtoBasic.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .category(movie.getCategory().toString())
                .build();
    }
    public static MovieDtoWithUserRate mapToMovieDtoWithUserRate(MovieRate movieRate){
        return MovieDtoWithUserRate.builder()
                .id(movieRate.getMovie().getMovieId())
                .title(movieRate.getMovie().getTitle())
                .userRate(movieRate.getRate())
                .build();
    }
}

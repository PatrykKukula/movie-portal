package pl.patrykkukula.MovieReviewPortal.Mapper;

import lombok.Data;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateDateField;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

@Data
public class MovieMapper {

    public static Movie mapToMovie(MovieDto movieDto){
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        if (movieDto.getDescription() != null) {
            movie.setDescription(movieDto.getDescription());
        }
        movie.setReleaseDate(movieDto.getReleaseDate());
        return movie;
    }
    public static MovieDtoWithDetails mapToMovieDtoWithDetails(Movie movie, Double rate){
        Director director = movie.getDirector();
        DirectorDto directorDto = new DirectorDto();
        if (director != null) {
            directorDto = DirectorMapper.mapToDirectorDto(director);
        }

        return MovieDtoWithDetails.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .rating(rate)
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
        return MovieDto.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .category(movie.getCategory())
                .releaseDate(movie.getReleaseDate())
                .director(DirectorMapper.mapToDirectorSummary(movie.getDirector()))
                .actors(
                        movie.getActors().stream().map(ActorMapper::mapToActorSummary).toList())
                .build();
    }
    public static Movie mapToMovieUpdate(MovieDto movieDto, Movie movie){
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
}

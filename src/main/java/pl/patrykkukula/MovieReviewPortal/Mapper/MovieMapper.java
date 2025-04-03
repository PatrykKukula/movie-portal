package pl.patrykkukula.MovieReviewPortal.Mapper;
import lombok.Data;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto.MovieUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.updateField;

@Data
public class MovieMapper {

    public static Movie mapToMovie(MovieDto movieDto){
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setReleaseDate(movieDto.getReleaseDate());
        return movie;
    }
    public static MovieDtoWithDetails mapToMovieDtoWithDetails(Movie movie, Double rate){
        return MovieDtoWithDetails.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .rating(rate)
                .releaseDate(movie.getReleaseDate())
                .category(movie.getCategory().toString())
                .description(movie.getDescription())
                .director(movie.getDirector().getFirstName() + " " + movie.getDirector().getLastName())
                .actors(
                        movie.getActors().stream()
                                .map(actor -> actor.getFirstName() + " " + actor.getLastName())
                                .toList()
                )
                .build();
    }
    public static MovieDto mapToMovieDto(Movie movie){
        return MovieDto.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .id(movie.getMovieId())
                .build();
    }
    public static Movie mapToMovieUpdate(MovieUpdateDto movieUpdateDto, Movie movie){
        updateField(movieUpdateDto::getTitle, movie::setTitle);
        updateField(movieUpdateDto::getDescription, movie::setDescription);
        updateField(movieUpdateDto::getReleaseDate, movie::setReleaseDate);
        return movie;
    }
    public static MovieDtoBasic mapToMovieDtoBasic(Movie movie){
        return MovieDtoBasic.builder()
                .id(movie.getMovieId())
                .title(movie.getTitle())
                .build();
    }
}

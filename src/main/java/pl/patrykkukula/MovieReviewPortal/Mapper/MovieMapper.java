package pl.patrykkukula.MovieReviewPortal.Mapper;

import lombok.Data;
import pl.patrykkukula.MovieReviewPortal.Dto.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

@Data
public class MovieMapper {

    public static Movie mapToMovie(MovieDto movieDto){
        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setReleaseDate(movieDto.getReleaseDate());
        return movie;
    }
    public static MovieDto mapToMovieDto(Movie movie){
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle(movie.getTitle());
        movieDto.setDescription(movie.getDescription());
        movieDto.setReleaseDate(movie.getReleaseDate());
        movieDto.setDirector(movie.getDirector().getFirstName() + movie.getDirector().getLastName());
        movieDto.setCategory(movie.getCategory());
        return movieDto;
    }

}

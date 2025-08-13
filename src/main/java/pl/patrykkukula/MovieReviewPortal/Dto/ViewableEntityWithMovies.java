package pl.patrykkukula.MovieReviewPortal.Dto;

import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;

import java.time.LocalDate;
import java.util.List;

public interface ViewableEntityWithMovies {
    Long getId();
    String getFirstName();
    String getLastName();
    LocalDate getDateOfBirth();
    String getCountry();
    String getBiography();
    Double getRating();
    Integer getRateNumber();
    List<MovieDtoBasic> getMovies ();
}

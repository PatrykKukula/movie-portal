package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import lombok.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.ViewableEntityWithMovies;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class DirectorDtoWithMovies implements ViewableEntityWithMovies {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
    private String biography;
    private Double rating;
    private Integer rateNumber;
    private List<MovieDtoBasic> movies = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }
    @Override
    public String getFirstName() {
        return firstName;
    }
    @Override
    public String getLastName() {
        return lastName;
    }
    @Override
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    @Override
    public String getCountry() {
        return country;
    }
    @Override
    public String getBiography() {
        return biography;
    }
    @Override
    public Double getRating() {
        return rating;
    }
    @Override
    public Integer getRateNumber() {
        return rateNumber;
    }
    @Override
    public List<MovieDtoBasic> getMovies() {
        return movies;
    }
}

package pl.patrykkukula.MovieReviewPortal.Dto.Actor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ActorDtoWithMovies {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
    private String biography;
    private Double rating;
    private Integer rateNumber;
    private List<MovieDtoBasic> movies = new ArrayList<>();
}

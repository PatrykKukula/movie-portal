package pl.patrykkukula.MovieReviewPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class DirectorDtoWithMovies {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
    private List<MovieDto> movies = new ArrayList<>();
}

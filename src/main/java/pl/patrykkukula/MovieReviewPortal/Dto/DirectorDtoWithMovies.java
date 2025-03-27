package pl.patrykkukula.MovieReviewPortal.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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
    @NotEmpty(message = "Director name cannot be null or empty")
    private String firstName;
    @NotEmpty(message = "Director last cannot be null or empty")
    private String lastName;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$",message = "Date of birth must have format YYYY-MM-DD")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    @NotEmpty(message = "Director country cannot be null or empty")
    private String country;

    private List<MovieDto> movies = new ArrayList<>();
}

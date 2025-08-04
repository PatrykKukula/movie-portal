package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectorViewDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
    private String biography;
    private Double rate;
    private MovieCategory category;
}

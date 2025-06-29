package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @ToString
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public class DirectorUpdateDto {
    private String firstName;
    private String lastName;
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    private String country;
    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;
}

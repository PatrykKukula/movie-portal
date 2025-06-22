package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @ToString
@EqualsAndHashCode
@AllArgsConstructor @NoArgsConstructor
@Builder
public class DirectorUpdateDto {
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    @NotEmpty(message = "Country cannot be empty")
    private String country;
    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;
}

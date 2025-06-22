package pl.patrykkukula.MovieReviewPortal.Dto.Actor;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ActorUpdateDto {
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the past")
    private LocalDate dateOfBirth;
    @NotEmpty(message = "Director country cannot be empty")
    private String country;
    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;
}

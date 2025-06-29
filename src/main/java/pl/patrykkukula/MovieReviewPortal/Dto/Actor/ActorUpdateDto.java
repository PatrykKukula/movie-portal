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
    private String firstName;
    private String lastName;
    @PastOrPresent(message = "Date of birth cannot be in the past")
    private LocalDate dateOfBirth;
    private String country;
    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;
}

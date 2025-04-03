package pl.patrykkukula.MovieReviewPortal.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ActorDto {
    private Long id;
    @NotEmpty(message = "Actor name cannot be null or empty")
    private String firstName;
    @NotEmpty(message = "Actor last cannot be null or empty")
    private String lastName;
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    @NotEmpty(message = "Actor country cannot be null or empty")
    private String country;
}

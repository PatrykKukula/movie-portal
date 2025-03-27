package pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$",message = "Date of birth must have format YYYY-MM-DD")
    @PastOrPresent(message = "Date of birth cannot be in the past")
    private LocalDate dateOfBirth;
    private String country;
}

package pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor
public class DirectorUpdateDto {
    private String firstName;
    private String lastName;
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    private String country;
}

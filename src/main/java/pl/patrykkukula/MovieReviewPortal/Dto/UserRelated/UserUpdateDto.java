package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDto {
    @Min(value = 1L, message = "ID cannot be less than 1")
    private Long id;
    @Email(message = "Invalid email format")
    private String email;
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    private UserSex userSex;
    private String firstName;
    private String lastName;
}

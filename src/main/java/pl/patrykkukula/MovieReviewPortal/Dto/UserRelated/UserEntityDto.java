package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;

import java.time.LocalDate;

import static pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants.*;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserEntityDto {
    @NotEmpty(message = "Username cannot be empty")
    @Pattern(regexp = USERNAME_REGEX, message = "Username can be made of alphanumeric characters and be 3-25 characters long")
    private String username;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = PASSWORD_REGEX,
    message = "Password must contain small and capital letter, number, special character and be at least 8 character long")
    private String password;
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;
    private UserSex userSex;
    private String firstName;
    private String lastName;
}

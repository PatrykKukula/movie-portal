package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.regex.qual.Regex;
import pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants;

import javax.annotation.MatchesPattern;

@Data
@AllArgsConstructor @Builder
public class LoginDto {
    @Email
    private String email;
    @Pattern(regexp = GlobalConstants.PASSWORD_REGEX,
            message = "Password must contain small and capital letter, number, special character and be at lest 8 character long")
    private String password;
}

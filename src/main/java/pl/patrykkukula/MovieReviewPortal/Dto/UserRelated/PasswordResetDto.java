package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetDto {
    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = GlobalConstants.PASSWORD_REGEX,
            message = "Password must contain small and capital letter, number, special character and be at lest 8 character long")
    private String newPassword;
    @NotEmpty(message = "Token cannot be empty")
    private String pwdResetToken;
}

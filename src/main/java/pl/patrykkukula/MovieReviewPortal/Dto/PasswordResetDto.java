package pl.patrykkukula.MovieReviewPortal.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetDto {
    @NotEmpty(message = "Password cannot be null or empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-+=?.><]){8,}$",
            message = "Password must contain small and capital letter, number, special character and be at lest 8 character long")
    private String password;
    @NotEmpty(message = "Token cannot be null or empty")
    private String pwdResetToken;
}

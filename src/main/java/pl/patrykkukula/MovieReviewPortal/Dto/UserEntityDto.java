package pl.patrykkukula.MovieReviewPortal.Dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserEntityDto {
    @NotEmpty(message = "Username cannot be null or empty")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,25}$", message = "Username cannot have special character and must be 3-25 characters long")
    private String username;
    @NotEmpty(message = "Email cannot be null or empty")
    @Email(message = "Invalid email format")
    private String email;
    @NotEmpty(message = "Password cannot be null or empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()\\-+=?.><]){8,}$",
    message = "Password must contain small and capital letter, number, special character and be at lest 8 character long")
    private String password;
}

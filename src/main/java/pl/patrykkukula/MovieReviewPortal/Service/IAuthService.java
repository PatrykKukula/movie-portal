package pl.patrykkukula.MovieReviewPortal.Service;

import org.springframework.security.core.Authentication;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;

public interface IAuthService {
    String register(UserEntityDto userDto);
    void verifyAccount(String token);
    String resendVerificationToken(String username);
    void resetPassword(PasswordResetDto passwordResetDto);
    String generatePasswordResetToken(String email);
    UserEntityDto getUserByEmail(Authentication authentication);
//    boolean login(String email, String password);
}

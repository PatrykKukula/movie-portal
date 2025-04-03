package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Dto.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserEntityDto;

public interface IAuthService {
    String register(UserEntityDto userDto);
    void verifyAccount(String token);
    String resendVerificationToken(String username);
    void resetPassword(PasswordResetDto passwordResetDto);
    String generatePasswordResetToken(String email);
}

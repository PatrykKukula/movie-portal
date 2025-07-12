package pl.patrykkukula.MovieReviewPortal.Service;

import org.springframework.security.core.Authentication;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

public interface IAuthService {
    String register(UserEntityDto userDto);
    void verifyAccount(String token);
    String resendVerificationToken(String username);
    void resetPassword(PasswordResetDto passwordResetDto);
    String generatePasswordResetToken(String email);
    boolean changePassword(UserEntity user, String newPassword);
    boolean changeEmail(UserEntity user, String newEmail);
//    boolean login(String email, String password);
}

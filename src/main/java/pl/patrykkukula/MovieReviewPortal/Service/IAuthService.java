package pl.patrykkukula.MovieReviewPortal.Service;

import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

import java.time.LocalDate;

public interface IAuthService {
    String register(UserEntityDto userDto);
    void verifyAccount(String token);
    String sendVerificationToken(String username);
    boolean resetPassword(PasswordResetDto passwordResetDto);
    String generatePasswordResetToken(String email);
    boolean changePassword(UserEntity user, String newPassword);
    boolean changeEmail(UserEntity user, String newEmail);
    boolean changeSex(UserEntity user, UserSex sex);
    boolean changeFirstName(UserEntity user, String newFirstName);
    boolean changeLastName(UserEntity user, String newLastName);
    boolean changeDateOfBirth(UserEntity user, LocalDate newDateOfBirth);
//    boolean login(String email, String password);
}

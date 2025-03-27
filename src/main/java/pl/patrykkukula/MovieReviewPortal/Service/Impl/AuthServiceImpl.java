package pl.patrykkukula.MovieReviewPortal.Service.Impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.PasswordResetToken;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Model.VerificationToken;
import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.RoleRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserEntityRepository userRepository;
    private final VerificationTokenRepository verificationRepository;
    private final PasswordResetRepository pwdRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void register(UserEntityDto userDto) {
        Optional<UserEntity> existingUser = userRepository.findUserByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if (existingUser.isPresent()) throw new IllegalStateException("Username or email already exists");

        String hashedPassword = encoder.encode(userDto.getPassword());
        Role role = roleRepository.findRoleByRoleName("USER").orElseThrow(() -> new RuntimeException("Role USER not found. Please contact technical support"));

       UserEntity user = UserEntity.builder()
                        .username(userDto.getUsername())
                        .password(hashedPassword)
                        .email(userDto.getEmail())
                        .roles(List.of(role))
                        .build();
        userRepository.save(user);

       generateVerificationToken(user);
    }
    @Override
    @Transactional
    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid or expired token"));
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) throw new IllegalStateException("Token expired");
        UserEntity userEntity = verificationToken.getUser();

        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        verificationRepository.delete(verificationToken);
    }
    @Override
    @Transactional
    public void resendVerificationToken(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Username", "username", username));
        generateVerificationToken(user);
    }
    @Override
    @Transactional
    public void resetPassword(PasswordResetDto passwordResetDto) {
        PasswordResetToken token = pwdRepository.findByTokenWithUser(passwordResetDto.getPwdResetToken())
                .orElseThrow(() -> new IllegalStateException("Invalid token"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) throw new IllegalStateException("Token expired");
        UserEntity user = token.getUser();
        String hashedPassword = encoder.encode(passwordResetDto.getPassword());
        if (hashedPassword.equals(user.getPassword())) throw new IllegalStateException("New password cannot be same as old password");
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }
    @Override
    public String generatePasswordResetToken(String email){
        UserEntity user = userRepository.findByUsername(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        pwdRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        PasswordResetToken pwdToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        pwdRepository.save(pwdToken);
        return "Password reset token has been created and will be valid for 12 hours: " + token + System.lineSeparator() +
        "Please input token and new password in form";
    }
    private void generateVerificationToken(UserEntity user){
        if (user.isEnabled()) throw new IllegalStateException("Account is verified");
        verificationRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken
                .builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        verificationRepository.save(verificationToken);
        String activationLink = "http://localhost:8080/register/confirm?token=" + token;
        System.out.println("Verification token has been created and will be valid for 12 hours: ");
        System.out.println("Click the link to activate account: " + activationLink);
    }
}

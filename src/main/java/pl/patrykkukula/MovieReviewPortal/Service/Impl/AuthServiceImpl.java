package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.LoginDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.UserEntityMapper;
import pl.patrykkukula.MovieReviewPortal.Mapper.UserMapper;
import pl.patrykkukula.MovieReviewPortal.Model.PasswordResetToken;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Model.VerificationToken;
import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.RoleRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.Utils.JwtUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants.ONLY_LETTER_REGEX;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserEntityRepository userRepository;
    private final VerificationTokenRepository verificationRepository;
    private final PasswordResetRepository pwdRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
//    private final JavaMailSender mailSender;
//    @Value("${app.mail.from}")
//    private String from;

    @Override
    public String login(LoginDto loginDto) {
        UserEntity user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", loginDto.getEmail()));
        if (!encoder.matches(loginDto.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for user: {}", loginDto.getEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
        if (!user.isEnabled()) throw new IllegalStateException("You are not verified - please verify your account");
        if (user.getBanned() != null && user.getBanned()) throw new IllegalStateException("Your account is banned and will be back available at: " + user.getBanExpiration());

        List<? extends GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName())).toList();

        log.info("User {} logged in successfully", user.getEmail());

        return jwtUtils.generateJwtToken(user.getEmail(), authorities);
    }
    @Override
    @Transactional
    public String register(UserEntityDto userDto) {
        Optional<UserEntity> existingUser = userRepository.findUserByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if (existingUser.isPresent()) throw new IllegalStateException("Username or email already exists");

        String hashedPassword = encoder.encode(userDto.getPassword());
        Role role = roleRepository.findRoleByRoleName("USER").orElseThrow(() -> new RuntimeException("Role USER not found. Please contact technical support"));

        UserEntity user = UserEntityMapper.mapToUserEntity(userDto);
        user.setPassword(hashedPassword);
        user.setRoles(List.of(role));
        try {
           userRepository.save(user);
        }
        catch (DataIntegrityViolationException ex){
           throw new IllegalStateException("Username or email already exists");
        }
        return generateVerificationToken(user);
    }
    @Override
    @Transactional
    public void verifyAccount(String token) {
        if (token == null || token.isEmpty()) throw new IllegalArgumentException("token cannot be null or empty");
        VerificationToken verificationToken = verificationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("verification token not found"));
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) throw new IllegalStateException("Token expired");
        UserEntity userEntity = verificationToken.getUser();
        if (userEntity.isEnabled()) throw new IllegalStateException("Account already verified");
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        verificationRepository.delete(verificationToken);
    }
    @Override
    @Transactional
    public String sendVerificationToken(String email) {
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("email cannot be null or empty");
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Account", "email", email));
        return resendVerificationToken(user);
    }
    @Override
    @Transactional
    public boolean resetPassword(PasswordResetDto passwordResetDto) {
        PasswordResetToken token = pwdRepository.findByToken(passwordResetDto.getPwdResetToken())
                .orElseThrow(() -> new IllegalStateException("Password reset token not found"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) throw new IllegalStateException("Password reset token expired");
        UserEntity user = token.getUser();

        if (!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())){
            throw new IllegalStateException("Passwords doesn't match");
        }

        if (encoder.matches(passwordResetDto.getNewPassword(), user.getPassword())) throw new IllegalStateException("New password cannot be same as old password");
        String hashedNewPassword = encoder.encode(passwordResetDto.getNewPassword());
        user.setPassword(hashedNewPassword);
        pwdRepository.delete(token);
        userRepository.save(user);
        return true;
    }
    @Override
    @Transactional(rollbackOn = IllegalStateException.class)
    public String generatePasswordResetToken(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        Optional<PasswordResetToken> existingToken = pwdRepository.findByUser(user);
        existingToken.ifPresent(token -> {
//            user.setPasswordResetToken(null);
//            userRepository.save(user);
            pwdRepository.delete(token);
            pwdRepository.flush();
        });
        String token = UUID.randomUUID().toString();
        PasswordResetToken pwdToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        pwdRepository.save(pwdToken);
        return token;
//        return "Password reset token has been created and will be valid for 12 hours: " + token + lineSeparator() +
//                "To reset your password, send a POST request to the following URL: " +
//                "http://localhost:8081/auth/reset with the following JSON body: " +
//                "{\"password\":" + "your new password" + lineSeparator() +
//                "\"token\": \"" + token + "\"" + "}";
    }
    /*
    REST API Section
 */
    @Override
    public void updateUserData(UserUpdateDto userUpdateDto) {
        validateId(userUpdateDto.getId());
        UserEntity user = userRepository.findById(userUpdateDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userUpdateDto.getId().toString()));

        UserEntity updatedUser = UserMapper.mapUserUpdateDtoToUserEntity(user, userUpdateDto);
        userRepository.save(updatedUser);
    }
    /*
        Vaadin section
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public boolean changePassword(UserEntity user, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) return false;
        Pattern pattern = Pattern.compile(GlobalConstants.PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(newPassword);
        boolean matches = matcher.matches();
        if (matches) {
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public boolean changeEmail(UserEntity user, String newEmail) {
        Pattern pattern = Pattern.compile(GlobalConstants.EMAIL_REGEX);
        Matcher matcher = pattern.matcher(newEmail);
        if (matcher.matches()) {
            try {
                user.setEmail(newEmail);
                userRepository.save(user);
                return true;
            }
            catch (DataIntegrityViolationException ex){
                throw new IllegalStateException("Email already exists");
            }
        }
        return false;
    }
    @Override
    public boolean changeSex(UserEntity user, UserSex sex) {
        try {
            user.setUserSex(sex);
            userRepository.save(user);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
    @Override
    public boolean changeFirstName(UserEntity user, String newFirstName) {
        Pattern pattern = Pattern.compile(ONLY_LETTER_REGEX);
        Matcher matcher = pattern.matcher(newFirstName);
        if (matcher.matches()) {
            user.setFirstName(newFirstName);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    public boolean changeLastName(UserEntity user, String newLastName) {
        Pattern pattern = Pattern.compile(ONLY_LETTER_REGEX);
        Matcher matcher = pattern.matcher(newLastName);
        if (matcher.matches()) {
            user.setLastName(newLastName);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    public boolean changeDateOfBirth(UserEntity user, LocalDate newDateOfBirth) {
        if (newDateOfBirth != null && newDateOfBirth.isBefore(LocalDate.now())){
            user.setDateOfBirth(newDateOfBirth);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    private String generateVerificationToken(UserEntity user){
        if (user.isEnabled()) throw new IllegalStateException("Account is verified");

        return verificationTokenBase(user);
    }
    private String resendVerificationToken(UserEntity user){
        if (user.isEnabled()) throw new IllegalStateException("Account already verified");
        userRepository.save(user);
        verificationRepository.flush();

     return verificationTokenBase(user);
    }
    private String verificationTokenBase(UserEntity user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken
                .builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        verificationRepository.save(verificationToken);

//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//        try {
//            helper.setTo("kukla1416@wp.pl");
//            helper.setText("This is test message");
//            helper.setSubject("Verify account");
//            helper.setFrom(from);
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//        mailSender.send(mimeMessage);
//        try {
//            log.info("Sending email message:{} ", mimeMessage.getSender());
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
        return token;
//        return  "Verification token has been created and will be valid for 12 hours: " + lineSeparator() +
//                "To activate your account, send a POST request to the following URL: " +
//                "http://localhost:8081/auth/register/confirm with the following JSON body: " +
//                "{ \"token\": \"" + token + "\" }";
    }
}

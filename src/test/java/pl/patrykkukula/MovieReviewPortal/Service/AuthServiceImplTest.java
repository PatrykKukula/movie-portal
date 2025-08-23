//package pl.patrykkukula.MovieReviewPortal.Service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
//import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
//import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
//import pl.patrykkukula.MovieReviewPortal.Model.PasswordResetToken;
//import pl.patrykkukula.MovieReviewPortal.Model.Role;
//import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
//import pl.patrykkukula.MovieReviewPortal.Model.VerificationToken;
//import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
//import pl.patrykkukula.MovieReviewPortal.Repository.RoleRepository;
//import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
//import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;
//import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthServiceImplTest {
//    @Mock
//    private UserEntityRepository userRepository;
//    @Mock
//    private VerificationTokenRepository verificationRepository;
//    @Mock
//    private PasswordResetRepository pwdRepository;
//    @Mock
//    private RoleRepository roleRepository;
//    @Mock
//    PasswordEncoder passwordEncoder;
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    private UserEntityDto userEntityDto;
//    private Role role;
//    private VerificationToken verificationToken;
//    private PasswordResetToken passwordResetToken;
//
//    @BeforeEach
//    void setUp(){
//        userEntityDto = UserEntityDto.builder()
//                .username("Username")
//                .email("email@email.com")
//                .password("Password123!")
//                .build();
//        role = Role.builder()
//                .roleId(1L)
//                .roleName("USER")
//                .build();
//        verificationToken = VerificationToken.builder()
//                .tokenId(1L)
//                .token(UUID.randomUUID().toString())
//                .expiryDate(LocalDateTime.now().plusHours(12))
//                .user(setCurrentUser())
//                .build();
//        passwordResetToken = PasswordResetToken.builder()
//                .pwdToken(1L)
//                .token(UUID.randomUUID().toString())
//                .expiryDate(LocalDateTime.now().plusHours(12))
//                .user(setCurrentUser())
//                .build();
//    }
//
//    @Test
//    public void shouldRegisterUserCorrectly(){
//        when(userRepository
//                .findUserByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
//        when(roleRepository.findRoleByRoleName(anyString())).thenReturn(Optional.of(role));
//        when(userRepository.save(any(UserEntity.class))).thenReturn(setCurrentUser());
//        when(verificationRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
//
//        String returnMessage = authService.register(userEntityDto);
//
//        assertTrue(returnMessage.contains("Verification token has been created and will be valid for 12 hours: "));
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenUsernameIsTaken(){
//        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(setCurrentUser()));
//
//        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.register(userEntityDto));
//        assertEquals("Username or email already exists", ex.getMessage());
//    }
//    @Test
//    public void shouldThrowRuntimeExceptionWhenRoleNotFound(){
//        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(userEntityDto));
//        assertEquals("Role USER not found. Please contact technical support", ex.getMessage());
//    }
//    @Test
//    public void shouldResendVerificationTokenCorrectly(){
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        when(verificationRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);
//
//        String returnMessage = authService.sendVerificationToken("email@email.com");
//
//        assertTrue(returnMessage.contains("Verification token has been created and will be valid for 12 hours: "));
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenResendVerificationTokenAndUserIsEnabled(){
//        UserEntity user = setCurrentUser();
//        user.setEnabled(true);
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
//
//        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.sendVerificationToken("email@email.com"));
//        assertEquals("Account is verified", ex.getMessage());
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenVerificationTokenIsExpired(){
//        verificationToken.setExpiryDate(LocalDateTime.now().minusHours(12));
//        when(verificationRepository.findByToken(anyString())).thenReturn(Optional.of(verificationToken));
//
//        assertThrows(IllegalStateException.class, () -> authService.verifyAccount(verificationToken.getToken()));
//    }
//    @Test
//    public void shouldGeneratePasswordResetTokenCorrectly(){
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        when(pwdRepository.findByUser(any(UserEntity.class))).thenReturn(Optional.empty());
//        when(pwdRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
//
//        String returnMessage = authService.generatePasswordResetToken("email@email.com");
//
//        assertTrue(returnMessage.contains("Password reset token has been created and will be valid for 12 hours: "));
//    }
//    @Test
//    public void shouldThrowResourceNotFoundExceptionWhenUserNotFound(){
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> authService.generatePasswordResetToken("email@email.com"));
//    }
//    @Test
//    public void shouldResetPasswordCorrectly(){
//        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
//        when(passwordEncoder.encode(anyString())).thenReturn("Newpassword123!");
//        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
//
//        authService.resetPassword(setPasswordResetDto());
//        verify(userRepository).save(captor.capture());
//
//        assertEquals("Newpassword123!", captor.getValue().getPassword());
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenPasswordResetTokenIsInvalid(){
//        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.empty());
//
//        assertThrows(IllegalStateException.class, () -> authService.resetPassword(setPasswordResetDto()));
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenInputPasswordIsSameAsCurrentPassword(){
//        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
//        PasswordResetDto passwordResetDto = setPasswordResetDto();
//        passwordResetDto.setNewPassword("Password123!");
//
//        assertThrows(IllegalStateException.class, () -> authService.resetPassword(passwordResetDto));
//    }
//    @Test
//    public void shouldThrowIllegalStateExceptionWhenPasswordResetTokenIsExpired(){
//        passwordResetToken.setExpiryDate(LocalDateTime.now().minusHours(12));
//        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
//
//        assertThrows(IllegalStateException.class, () -> authService.resetPassword(setPasswordResetDto()));
//    }
//    @Test
//    public void passwordResetTokenShouldBeNullAfterSuccessfulReset(){
//        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(setCurrentUser()));
//        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
//
//        authService.resetPassword(setPasswordResetDto());
//        verify(userRepository).save(captor.capture());
//
//        assertNull(captor.getValue().getPasswordResetToken());
//    }
//    private UserEntity setCurrentUser() {
//        Role role = Role.builder()
//                .roleId(1L)
//                .roleName("USER")
//                .build();
//
//        return UserEntity.builder()
//                .userId(1L)
//                .email("user@user.com")
//                .password("Password123!")
//                .roles(List.of(role))
//                .isEnabled(false)
//                .build();
//    }
//    private PasswordResetDto setPasswordResetDto(){
//        return PasswordResetDto.builder()
//                .email("email@email.com")
//                .newPassword("Newpassword123!")
//                .pwdResetToken(passwordResetToken.getToken())
//                .build();
//    }
//}

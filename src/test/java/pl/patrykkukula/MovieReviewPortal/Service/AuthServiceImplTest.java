package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.LoginDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.PasswordResetToken;
import pl.patrykkukula.MovieReviewPortal.Model.Role;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Model.VerificationToken;
import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.RoleRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Utils.JwtUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserEntityRepository userRepository;
    @Mock
    private VerificationTokenRepository verificationRepository;
    @Mock
    private PasswordResetRepository pwdRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private BCryptPasswordEncoder encoder;
    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntityDto userEntityDto;
    private UserEntity user;
    private PasswordResetDto passwordResetDto;
    private Role role;
    private LoginDto loginDto;
    private UserUpdateDto userUpdateDto;
    private VerificationToken verificationToken;
    private PasswordResetToken passwordResetToken;
    private Authentication authentication;

    @BeforeEach
    void setUp(){
        SecurityContextHolder.setContext(securityContext);
        userEntityDto = UserEntityDto.builder()
                .username("Username")
                .email("email@email.com")
                .password("Password123!")
                .build();
        role = Role.builder()
                .roleId(1L)
                .roleName("USER")
                .build();

        user = setCurrentUser();
        verificationToken = VerificationToken.builder()
                .tokenId(1L)
                .token("token")
                .expiryDate(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        passwordResetDto = setPasswordResetDto();
        passwordResetToken = PasswordResetToken.builder()
                .pwdToken(1L)
                .token("token")
                .expiryDate(LocalDateTime.now().plusHours(12))
                .user(user)
                .build();
        loginDto = LoginDto.builder()
                .email("email")
                .password("password")
                .build();
        authentication = new UsernamePasswordAuthenticationToken("username", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        userUpdateDto = UserUpdateDto.builder()
                .id(1L)
                .email("updated")
                .build();
    }
    @AfterEach
    public void clear(){
        SecurityContextHolder.clearContext();
    }
    @Test
    public void shouldLoginCorrectly(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtils.generateJwtToken(anyString(), anyList())).thenReturn("token-is-here");

        String token = authService.login(loginDto);

        assertEquals("token-is-here", token);
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenLoginAndUserNotFount(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> authService.login(loginDto));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenLoginAndUserIsNotEnabled(){
        user.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.login(loginDto));
        assertEquals("You are not verified - please verify your account", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenLoginAndUserIsBanned(){
        user.setBanned(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.login(loginDto));
        assertTrue(ex.getMessage().contains("Your account is banned"));
    }
    @Test
    public void shouldRegisterUserCorrectly(){
        when(userRepository
                .findUserByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(roleRepository.findRoleByRoleName(anyString())).thenReturn(Optional.of(role));
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        String token = authService.register(userEntityDto);

        assertFalse(token.isEmpty());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenRegisterAndUsernameIsTaken(){
        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.register(userEntityDto));
        assertEquals("Username or email already exists", ex.getMessage());
    }
    @Test
    public void shouldThrowRuntimeExceptionWhenRoleNotFound(){
        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(userEntityDto));
        assertEquals("Role USER not found. Please contact technical support", ex.getMessage());
    }
    @Test
    public void shouldVerifyAccountCorrectly(){
        user.setEnabled(false);
        verificationToken.setUser(user);
        when(verificationRepository.findByToken(anyString())).thenReturn(Optional.of(verificationToken));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        doNothing().when(verificationRepository).delete(any(VerificationToken.class));

        authService.verifyAccount("token");

        verify(userRepository, times(1)).save(user);
        verify(verificationRepository, times(1)).delete(verificationToken);
    }
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenVerifyAccountAndTokenIsNull(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.verifyAccount(null));
        assertEquals("token cannot be null or empty", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenVerifyAccountAndTokenIsExpired(){
        verificationToken.setExpiryDate(LocalDateTime.now().minusHours(24));
        when(verificationRepository.findByToken(anyString())).thenReturn(Optional.of(verificationToken));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.verifyAccount("token"));
        assertEquals("Token expired", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenVerifyAccountAndTokenNotFound(){
        when(verificationRepository.findByToken(anyString())).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.verifyAccount("token"));
        assertEquals("verification token not found", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenVerifyAccountAndAccountAlreadyVerified(){
        verificationToken.setUser(user);
        when(verificationRepository.findByToken(anyString())).thenReturn(Optional.of(verificationToken));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.verifyAccount("token"));
        assertEquals("Account already verified", ex.getMessage());
    }
    @Test
    public void shouldSendVerificationTokenCorrectly(){
        user.setEnabled(false);
        verificationToken.setUser(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        String token = authService.sendVerificationToken("email");

        assertFalse(token.isEmpty());
    }
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSendVerificationTokenAndEmailIsNull(){
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.sendVerificationToken(null));
        assertEquals("email cannot be null or empty", ex.getMessage());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenSendVerificationTokenAndEmailNotFound(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> authService.sendVerificationToken("email"));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenSendVerificationTokenAndUserIsEnabled(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.sendVerificationToken(("token")));
        assertEquals("Account already verified", ex.getMessage());
    }
    @Test
    public void shouldResetPasswordCorrectly(){
        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("newpassword");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        authService.resetPassword(passwordResetDto);
        verify(userRepository).save(captor.capture());

        assertEquals("newpassword", captor.getValue().getPassword());
    }
    @Test
    public void shouldTrowIllegalStateExceptionWhenResetPasswordAndTokenNotFound(){
        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.resetPassword(passwordResetDto));
        assertEquals("Password reset token not found", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenResetPasswordAndTokenIsExpired(){
        passwordResetToken.setExpiryDate(LocalDateTime.now().minusHours(24));
        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.resetPassword(passwordResetDto));
        assertEquals("Password reset token expired", ex.getMessage());
    }
    @Test
    public void shouldThrowIllegalStateExceptionWhenResetPasswordAndNewPasswordMatchOld(){
        passwordResetDto.setNewPassword("password");
        passwordResetDto.setConfirmPassword("password");
        when(pwdRepository.findByToken(anyString())).thenReturn(Optional.of(passwordResetToken));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> authService.resetPassword(passwordResetDto));
        assertEquals("New password cannot be same as old password", ex.getMessage());
    }
    @Test
    public void shouldGeneratePasswordResetTokenCorrectly(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(pwdRepository.findByUser(any(UserEntity.class))).thenReturn(Optional.empty());
        when(pwdRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);

        String token = authService.generatePasswordResetToken("email@email.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(36, token.length());
        verifyNoMoreInteractions(pwdRepository);
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenGeneratePasswordResetTokenAndUserNotFound(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> authService.generatePasswordResetToken("email"));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldGeneratePasswordResetTokenCorrectlyWhenTokenAlreadyExist(){
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(pwdRepository.findByUser(any(UserEntity.class))).thenReturn(Optional.of(passwordResetToken));

        String token = authService.generatePasswordResetToken("email@email.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(36, token.length());
        verify(pwdRepository, times(1)).delete(passwordResetToken);
    }
    @Test
    public void shouldUpdateUserDataCorrectlyWithSingleBodyValue(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        authService.updateUserData(userUpdateDto);
        verify(userRepository).save(captor.capture());
        UserEntity updateUser = captor.getValue();

        assertEquals("updated", updateUser.getEmail());
        assertEquals("Alex", updateUser.getFirstName());
    }
    @Test
    public void shouldUpdateUserDataCorrectlyWithFullBodyValue(){
        userUpdateDto.setFirstName("Ben");
        userUpdateDto.setLastName("Ten");
        userUpdateDto.setDateOfBirth(LocalDate.of(2005,1,1));
        userUpdateDto.setUserSex(UserSex.THEY);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        authService.updateUserData(userUpdateDto);
        verify(userRepository).save(captor.capture());
        UserEntity updateUser = captor.getValue();

        assertEquals("updated", updateUser.getEmail());
        assertEquals("Ben", updateUser.getFirstName());
        assertEquals("Ten", updateUser.getLastName());
        assertEquals(UserSex.THEY, updateUser.getUserSex());
        assertEquals(LocalDate.of(2005,1,1),updateUser.getDateOfBirth());
    }
    @Test
    public void shouldThrowResourceNotFoundExceptionWhenUpdateUserDataAndUserNotFount(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> authService.updateUserData(userUpdateDto));
        assertTrue(ex.getMessage().contains("not found"));
    }
    @Test
    public void shouldThrowInvalidIdExceptionWhenUpdateUserDataAndInvalidId(){
        userUpdateDto.setId(-1L);
        InvalidIdException ex = assertThrows(InvalidIdException.class, () -> authService.updateUserData(userUpdateDto));
        assertEquals("ID cannot be less than 1 or null", ex.getMessage());
    }
    @Test
    public void shouldChangePasswordCorrectly(){
        boolean reset = authService.changePassword(user, "Password123!");

        assertTrue(reset);
    }
    @Test
    public void shouldReturnFalseWhenChangePasswordAndNewPasswordIsNull(){
        boolean reset = authService.changePassword(user, null);

        assertFalse(reset);
    }
    @Test
    public void shouldReturnFalseWhenChangePasswordAndPasswordDoesNotMeetCondition(){
        boolean reset = authService.changePassword(user, "password");

        assertFalse(reset);
    }
    @Test
    public void shouldReturnFalserWhenChangePasswordAndPasswordIsTooShort(){
        boolean reset = authService.changePassword(user, "pass");

        assertFalse(reset);
    }
    @Test
    public void shouldChangeEmailCorrectly(){
        boolean reset = authService.changeEmail(user, "email@email.com");

        assertTrue(reset);
    }
    @Test
    public void shouldReturnFalseWhenChangeEmailAndEmailDoesNotMeetCondition(){
        boolean reset = authService.changeEmail(user, "email");

        assertFalse(reset);
    }
    @Test
    public void shouldChangeSexCorrectly(){
        boolean reset = authService.changeSex(user, UserSex.THEY);

        assertTrue(reset);
    }
    @Test
    public void shouldReturnFalseWhenChangeSexAndExceptionIsThrown(){
        when(userRepository.save(any(UserEntity.class))).thenThrow(RuntimeException.class);

        boolean reset = authService.changeSex(user, UserSex.THEY);

        assertFalse(reset);
    }
    @Test
    public void shouldChangeFirstNameCorrectly(){
        boolean reset = authService.changeFirstName(user, "Name");

        assertTrue(reset);
    }
    @Test
    public void shouldReturnFalseWhenChangeFirstNameAndNameDoesNotMeetCondition(){
        boolean reset = authService.changeFirstName(user, "123");

        assertFalse(reset);
    }
    @Test
    public void shouldChangeLastNameCorrectly(){
        boolean reset = authService.changeLastName(user, "Name");

        assertTrue(reset);
    }
    @Test
        public void shouldReturnFalseWhenChangeLastNameAndNameDoesNotMeetCondition(){
        boolean reset = authService.changeLastName(user, "123");

        assertFalse(reset);
    }
    @Test
    public void shouldChangeDateOfBirthCorrectly(){
        boolean reset = authService.changeDateOfBirth(user, LocalDate.of(1999, 1, 1));

        assertTrue(reset);
    }
    @Test
    public void shouldReturnFalseWhenChangeDateOfBirthAndDateIsInFuture(){
        boolean reset = authService.changeDateOfBirth(user, LocalDate.of(2099, 1, 1));

        assertFalse(reset);
    }

    private UserEntity setCurrentUser() {
        Role role = Role.builder()
                .roleId(1L)
                .roleName("USER")
                .build();

        return UserEntity.builder()
                .userId(1L)
                .username("user")
                .email("user@user.com")
                .password("password")
                .dateOfBirth(LocalDate.of(2000,1,1))
                .firstName("Alex")
                .lastName("Smith")
                .userSex(UserSex.HE)
                .roles(List.of(role))
                .isEnabled(true)
                .banned(false)
                .build();
    }
    private PasswordResetDto setPasswordResetDto(){
        return PasswordResetDto.builder()
                .newPassword("newpassword")
                .confirmPassword("newpassword")
                .pwdResetToken("token")
                .build();
    }
}

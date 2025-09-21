package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.LoginDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthServiceImpl authService;
    @Autowired
    private ObjectMapper mapper;

    private UserEntityDto userEntityDto;
    private LoginDto loginDto;
    private PasswordResetDto passwordResetDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp(){
        userEntityDto = UserEntityDto.builder()
                .username("Username")
                .email("email@email.com")
                .password("Password123!")
                .build();
        loginDto = LoginDto.builder()
                .email("email@email.com")
                .password("Password123!")
                .build();
        passwordResetDto = PasswordResetDto.builder()
                .newPassword("Password123!")
                .confirmPassword("Password123!")
                .pwdResetToken("token")
                .build();
        userUpdateDto = UserUpdateDto.builder()
                .id(1L)
                .email("email@email.com")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should login correctly")
    public void shouldLoginCorrectly() throws Exception {
        when(authService.login(any(LoginDto.class))).thenReturn("JWT token value");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginDto)))
                .andExpectAll(
                  status().isOk(),
                  content().string("JWT token value")
                );
        verify(authService, times(1)).login(any(LoginDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when login and service throws BadCredentialsException")
    public void shouldRespond400WhenLoginAndServiceThrowsBadCredentialsException() throws Exception {
        when(authService.login(any(LoginDto.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Invalid credentials"))
                );
        verify(authService, times(1)).login(any(LoginDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 404 when login and service throws ResourceNotFoundException")
    public void shouldRespond404WhenLoginAndServiceThrowsResourceNotFoundException() throws Exception {
        when(authService.login(any(LoginDto.class))).thenThrow(new ResourceNotFoundException("user", "user", "user"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginDto)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );
        verify(authService, times(1)).login(any(LoginDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should register user correctly")
    public void shouldRegisterUserCorrectly() throws Exception {
        when(authService.register(any(UserEntityDto.class))).thenReturn("Verification token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isOk(),
                        content().string("Verification token")
                );
        verify(authService, times(1)).register(any(UserEntityDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and service throws IllegalStateException")
    public void shouldRespond400WhenRegisterUserAndServiceThrowsIllegalStateException() throws Exception {
        when(authService.register(any(UserEntityDto.class))).thenThrow(new IllegalStateException("Username or email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Username or email already exists"))
                );
        verify(authService, times(1)).register(any(UserEntityDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and username is null")
    public void shouldRespond400WhenRegisterUserAndUsernameIsNull() throws Exception {
        userEntityDto.setUsername(null);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Username cannot be empty"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and username is too short")
    public void shouldRespond400WhenRegisterUserAndUsernameIsTooShort() throws Exception {
        userEntityDto.setUsername("a");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Username can be made of alphanumeric characters and be 3-25 characters long"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and username is too long")
    public void shouldRespond400WhenRegisterUserAndUsernameIsTooLong() throws Exception {
        userEntityDto.setUsername("a".repeat(26));
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Username can be made of alphanumeric characters and be 3-25 characters long"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and email format is invalid")
    public void shouldRespond400WhenRegisterUserAndEmailFormatIsInvalid() throws Exception {
        userEntityDto.setEmail("email");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Invalid email format"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and password is invalid")
    public void shouldRespond400WhenRegisterUserAndPasswordIsInvalid() throws Exception {
        userEntityDto.setPassword("password");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Password must contain small and capital letter, number, special character and be at least 8 character long"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when register user and date of birth is in the future")
    public void shouldRespond400WhenRegisterUserAndDateOfBirthIsInFuture() throws Exception {
        userEntityDto.setDateOfBirth(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userEntityDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString( "Date of birth cannot be in the future"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should verify account correctly")
    public void shouldVerifyAccountCorrectly() throws Exception {
        doNothing().when(authService).verifyAccount(anyString());

        mockMvc.perform(post("/api/auth/register/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("token", "token"))))
                .andExpectAll(
                        status().isOk(),
                        content().string("Account verified successfully")
                );
        verify(authService, times(1)).verifyAccount(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 500 when verify account with no body request")
    public void shouldRespond500WhenVerifyAccountWithNoRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/register/confirm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isInternalServerError(),
                        jsonPath("$.statusCode").value(500),
                        jsonPath("$.errorMessage").value(containsString("Required request body is missing"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when verify account and service throws IllegalStateException")
    public void shouldRespond400WhenVerifyAccountAndServiceThrowsIllegalStateException() throws Exception {
        doThrow(new IllegalStateException("verification token not found")).when(authService).verifyAccount(anyString());

        mockMvc.perform(post("/api/auth/register/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("token", "token"))))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("verification token not found")
                );
        verify(authService, times(1)).verifyAccount(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should resend verification token correctly")
    public void shouldResendVerificationTokenCorrectly() throws Exception {
        when(authService.sendVerificationToken(anyString())).thenReturn("verification token");

        mockMvc.perform(get("/api/auth/register/sendToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "email"))
                .andExpectAll(
                        status().isOk(),
                        content().string("verification token")
                );
        verify(authService, times(1)).sendVerificationToken(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when resend verification token and service throws IllegalStateException")
    public void shouldRespond400WhenResendVerificationTokenAndServiceThrowsIllegalStateException() throws Exception {
        when(authService.sendVerificationToken(anyString())).thenThrow(new IllegalStateException("email cannot be null or empty"));

        mockMvc.perform(get("/api/auth/register/sendToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "user"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("email cannot be null or empty"));

        verify(authService, times(1)).sendVerificationToken(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when resend verification token and service throws ResourceNotFoundException")
    public void shouldRespond404WhenResendVerificationTokenAndServiceThrowsResourceNotFoundException() throws Exception {
        when(authService.sendVerificationToken(anyString())).thenThrow(new IllegalStateException("email cannot be null or empty"));

        mockMvc.perform(get("/api/auth/register/sendToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "user"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("email cannot be null or empty"));

        verify(authService, times(1)).sendVerificationToken(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should reset password correctly")
    public void shouldResetPasswordCorrectly() throws Exception {
        when(authService.resetPassword(any(PasswordResetDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordResetDto)))
                .andExpectAll(
                        status().isOk(),
                        content().string("Password reset successfully")
                );
        verify(authService, times(1)).resetPassword(any(PasswordResetDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when reset password and new password is null")
    public void shouldRespond400WhenResetPasswordAndNewPasswordIsNull() throws Exception {
        passwordResetDto.setNewPassword(null);

        mockMvc.perform(post("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordResetDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Password cannot be empty"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when reset password and new password is invalid")
    public void shouldRespond400WhenResetPasswordAndNewPasswordIsInvalid() throws Exception {
        passwordResetDto.setNewPassword("password");

        mockMvc.perform(post("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordResetDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Password must contain small and capital letter, number, special character and be at lest 8 character long"))
                );
        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when reset password and service throw IllegalStateException")
    public void shouldRespond400WhenResetPasswordAndServiceThrowsIllegalStateException() throws Exception {
        when(authService.resetPassword(any(PasswordResetDto.class))).thenThrow(new IllegalStateException("Password reset token not found"));

        mockMvc.perform(post("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordResetDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("Password reset token not found"));

        verify(authService, times(1)).resetPassword(any(PasswordResetDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should send pwd reset token correctly")
    public void shouldSendPwdResetTokenCorrectly() throws Exception {
        when(authService.generatePasswordResetToken(anyString())).thenReturn("token");

        mockMvc.perform(get("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "email"))
                .andExpectAll(
                        status().isOk(),
                        content().string("token")
                );

        verify(authService, times(1)).generatePasswordResetToken(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond  400 when send pwd reset token and required param is empty")
    public void shouldRespond400WhenSendPwdResetTokenCorrectlyAndRequiredParamIsEmpty() throws Exception {

        mockMvc.perform(get("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Required request parameter"))
                );

        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond  404 when send pwd reset token and service throws ResourceNotFoundException")
    public void shouldRespond404WhenSendPwdResetTokenCorrectlyAndServiceThrowsResourceNotFoundException() throws Exception {
        when(authService.generatePasswordResetToken(anyString())).thenThrow(new ResourceNotFoundException("token", "token", "token"));

        mockMvc.perform(get("/api/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "email"))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found"))
                );

        verify(authService, times(1)).generatePasswordResetToken(anyString());
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should update user data correctly")
    public void shouldUpdateUserDataCorrectly() throws Exception {
        mockMvc.perform(patch("/api/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.statusCode").value("202"),
                        jsonPath("$.statusMessage").value("Accepted"));

        verify(authService, times(1)).updateUserData(any(UserUpdateDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when update user data and ID is invalid")
    public void shouldRespond400WhenUpdateUserDataAndIdIsLessThenOne() throws Exception {
        userUpdateDto.setId(-1L);
        mockMvc.perform(patch("/api/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("ID cannot be less than 1")));

        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when update user data and ID is invalid")
    public void shouldRespond400WhenUpdateUserDataAndEmailFormatIsInvalid() throws Exception {
        userUpdateDto.setEmail("email");
        mockMvc.perform(patch("/api/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Invalid email format")));

        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when update user data and date of birth is in future")
    public void shouldRespond400WhenUpdateUserDataAndDateOfBirthIsInFuture() throws Exception {
        userUpdateDto.setDateOfBirth(LocalDate.now().plusDays(1));
        mockMvc.perform(patch("/api/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Date of birth cannot be in the future")));

        verifyNoInteractions(authService);
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 400 when update user data and service throws InvalidIdException")
    public void shouldRespond400WhenUpdateUserDataAndServiceThrowsInvalidIdException() throws Exception {
        doThrow(new InvalidIdException()).when(authService).updateUserData(any(UserUpdateDto.class));

        mockMvc.perform(patch("/api/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value("ID cannot be less than 1 or null"));

        verify(authService, times(1)).updateUserData(any(UserUpdateDto.class));
    }
    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 404 when update user data and service throws ResourceNotFoundException")
    public void shouldRespond404WhenUpdateUserDataAndServiceThrowsResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("user", "user", "user")).when(authService).updateUserData(any(UserUpdateDto.class));

        mockMvc.perform(patch("/api/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.errorMessage").value(containsString("not found")));

        verify(authService, times(1)).updateUserData(any(UserUpdateDto.class));
    }
}

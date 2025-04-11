package pl.patrykkukula.MovieReviewPortal.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.patrykkukula.MovieReviewPortal.Dto.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Service.AuthServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @BeforeEach
    void setUp(){
        userEntityDto = UserEntityDto.builder()
                .username("Username")
                .email("email@email.com")
                .password("Password123!")
                .build();
    }

    @Test
    public void shouldRegisterUserCorrectly() throws Exception{
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userEntityDto))).andExpectAll(
                status().isOk()
        );
    }
    @Test
    public void shouldRespond400WhenRegisterUserAndInvalidRequestBody() throws Exception{
        userEntityDto.setPassword("Password");
        userEntityDto.setEmail("email");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userEntityDto))).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.statusCode").value("400"),
                jsonPath("$.errorMessage").value(containsString("Invalid email format")),
                jsonPath("$.errorMessage").value(containsString("Password must contain small and capital"))
                );
    }
    @Test
    public void shouldRespond500WhenRegisterAndUsernameExists() throws Exception{
        doThrow(IllegalStateException.class).when(authService).register(Mockito.any(UserEntityDto.class));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userEntityDto))).andExpectAll(
                status().isInternalServerError(),
                jsonPath("$.statusCode").value("500"),
                jsonPath("$.statusMessage").value(containsString("Internal Server Error"))
        );
    }
    @Test
    public void shouldVerifyAccountCorrectly() throws Exception{
        Map<String,String> body = new HashMap<>();
        body.put("token", "mock token value");

        mockMvc.perform(post("/auth/register/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)))
                .andExpectAll(
                        status().isOk(),
                        content().string("Account verified successfully")
                );
    }
    @Test
    public void shouldResendVerificationTokenCorrectly() throws Exception {
        mockMvc.perform(get("/auth/register/sendToken")
                        .param("email", "email@email.com"))
                .andExpectAll(
                        status().isOk()
                );
    }
    @Test
    public void shouldResetPasswordCorrectly() throws Exception{
        PasswordResetDto passwordResetDto = PasswordResetDto.builder()
                        .newPassword("Newpassword123!")
                        .email("email@email.com")
                        .pwdResetToken("pwdResetToken")
                        .build();

        mockMvc.perform(post("/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordResetDto)))
                .andExpectAll(
                        status().isOk(),
                        content().string("Password reset successfully")
                );
    }
    @Test
    public void shouldRespond400WhenResetPasswordAndInvalidRequestBody() throws Exception{
        PasswordResetDto passwordResetDto = PasswordResetDto.builder()
                .newPassword("x")
                .email("email@email.com")
                .pwdResetToken("pwdResetToken")
                .build();

        mockMvc.perform(post("/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordResetDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Password must contain small and capital letter"))
                );
    }
    @Test
    public void shouldSendPwdResetTokenCorrectly() throws Exception{
        mockMvc.perform(get("/auth/reset")
                        .param("email","email@email.com"))
                .andExpectAll(
                        status().isOk()
                );
    }
    @Test
    public void shouldRespond400WhenSendPwdResetTokenWithNoRequestParam() throws Exception{
        mockMvc.perform(get("/auth/reset"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusCode").value("400"),
                        jsonPath("$.errorMessage").value(containsString("Required request parameter 'email' for method parameter"))
                );
    }
}

package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService registerService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserEntityDto userDto) {
        logger.info("AuthController - received POST /auth/register request");
        String register = registerService.register(userDto);
        logger.info(register);
        return ResponseEntity.ok(register);
    }
    @PostMapping("/register/confirm")
    public ResponseEntity<String> verifyAccount(@RequestBody Map<String,String> request) {
        String token = request.get("token");
        registerService.verifyAccount(token);
        return ResponseEntity.ok("Account verified successfully");
    }
    @GetMapping("/register/sendToken")
    public ResponseEntity<String> resendActivationLink(@RequestParam("email") String username) {
        return ResponseEntity.ok(registerService.sendVerificationToken(username));
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetDto passwordResetDto) {
        registerService.resetPassword(passwordResetDto);
        return ResponseEntity.ok("Password reset successfully");
    }
    @GetMapping("/reset")
    public ResponseEntity<String> sendPwdResetToken(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(registerService.generatePasswordResetToken(email));
    }
}

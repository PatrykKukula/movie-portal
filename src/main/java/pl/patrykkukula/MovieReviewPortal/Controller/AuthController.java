package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private IAuthService registerService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserEntityDto userDto) {
        return ResponseEntity.ok(registerService.register(userDto));
    }
    @PostMapping("/register/confirm")
    public ResponseEntity<String> verifyAccount(@RequestBody Map<String,String> request) {
        String token = request.get("token");
        registerService.verifyAccount(token);
        return ResponseEntity.ok("Account verified successfully");
    }
    @GetMapping("/register/sendToken")
    public ResponseEntity<String> resendActivationLink(@RequestParam("email") String username) {
        return ResponseEntity.ok(registerService.resendVerificationToken(username));
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

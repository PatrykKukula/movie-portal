package pl.patrykkukula.MovieReviewPortal.Controller;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.STATUS_201;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.STATUS_201_MESSAGE;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private IAuthService registerService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody UserEntityDto userDto) {
        registerService.register(userDto);
        return ResponseEntity.ok(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @PostMapping("/register/confirm")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        registerService.verifyAccount(token);
        return ResponseEntity.ok("Account verified successfully");
    }
    @GetMapping("/register/sendToken")
    public ResponseEntity<String> resendActivationLink(@RequestParam String username) {
        registerService.resendVerificationToken(username);
        return ResponseEntity.ok("Activation token sent");
    }
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        registerService.resetPassword(passwordResetDto);
        return ResponseEntity.ok("Password reset successfully");
    }
    @GetMapping("/reset")
    public ResponseEntity<String> sendPwdResetToken(@RequestParam String email) {
        String response = registerService.generatePasswordResetToken(email);
        return ResponseEntity.ok(response);
    }
}

package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.*;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;

import java.util.Map;

import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final IAuthService registerService;
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDto loginDto, HttpServletResponse response){
        String token = authServiceImpl.login(loginDto);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserEntityDto userDto) {
        String token = registerService.register(userDto);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/register/confirm")
    public ResponseEntity<String> verifyAccount(@RequestBody Map<String,String> request) {
        String token = request.get("token");
        registerService.verifyAccount(token);
        return ResponseEntity.ok("Account verified successfully");
    }
    @GetMapping("/register/sendToken")
    public ResponseEntity<String> resendVerificationToken(@RequestParam("email") String username) {
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
    @PatchMapping("/update")
    public ResponseEntity<ResponseDto> updateUserData(@RequestBody @Valid UserUpdateDto userUpdateDto){
        authServiceImpl.updateUserData(userUpdateDto);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class TokenCleanupService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetRepository passwordResetRepository;

    @Scheduled(fixedRate = 36000000)
    public void cleanupExpiredVerificationTokens() {
        verificationTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        passwordResetRepository.deleteExpiredTokens(LocalDateTime.now());
        System.out.println("Cleaned up expired tokens");
    }
}

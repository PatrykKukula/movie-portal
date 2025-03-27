package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenCleanupService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetRepository passwordResetRepository;

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredVerificationTokens() {
        verificationTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        passwordResetRepository.deleteExpiredTokens(LocalDateTime.now());
        System.out.println("Cleaned up expired tokens");
    }
}

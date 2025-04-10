package pl.patrykkukula.MovieReviewPortal.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.patrykkukula.MovieReviewPortal.Repository.PasswordResetRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.VerificationTokenRepository;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenCleanupServiceTest {
    @Mock
    private PasswordResetRepository passwordResetRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @InjectMocks
    private TokenCleanupService tokenCleanupService;

    @Test
    public void shouldCleanupExpiredTokensCorrectly(){
        tokenCleanupService.cleanupExpiredVerificationTokens();

        verify(verificationTokenRepository, times(1)).deleteExpiredTokens(any(LocalDateTime.class));
        verify(passwordResetRepository, times(1)).deleteExpiredTokens(any(LocalDateTime.class));
    }
}

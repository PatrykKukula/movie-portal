package pl.patrykkukula.MovieReviewPortal.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.PasswordResetToken;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(UserEntity user);
    @Query("SELECT v FROM VerificationToken v LEFT JOIN v.user WHERE v.token = :token")
    Optional<PasswordResetToken> findByTokenWithUser(@Param(value = "token") String token);

    @Transactional
    @Modifying
    @Query("SELECT v FROM VerificationToken v WHERE v.expiryDate < :now")
    void deleteExpiredTokens(@Param(value = "now") LocalDateTime now);
}

package pl.patrykkukula.MovieReviewPortal.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.VerificationToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    @Transactional
    @Modifying
    @Query("DELETE FROM VerificationToken v WHERE v.expiryDate < :now")
    void deleteExpiredTokens(@Param(value = "now") LocalDateTime now);
}

package pl.patrykkukula.MovieReviewPortal.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.ActorRate;
import java.util.Optional;

@Repository
public interface ActorRateRepository extends JpaRepository<ActorRate, Long> {
    @Query("SELECT ar FROM ActorRate ar WHERE ar.actor.actorId = :actorId AND ar.user.userId = :userId")
    Optional<ActorRate> findByActorIdAndUserId(@Param(value = "actorId") Long actorId, @Param(value = "userId") Long userId);
    @Modifying
    @Query("DELETE FROM ActorRate ar WHERE ar.actor.actorId = :actorId AND ar.user.userId = :userId")
    int deleteByActorIdAndUserId(@Param("actorId") Long actorId, @Param("userId") Long userId);
    @Query("SELECT COALESCE(AVG(ar.rate),0) FROM Actor a LEFT JOIN a.actorRates ar WHERE a.actorId = :actorId")
    double getAverageActorRate(@Param(value = "actorId") Long actorId);
}

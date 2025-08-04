package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.ActorRate;
import pl.patrykkukula.MovieReviewPortal.Model.DirectorRate;

import java.util.Optional;

@Repository
public interface DirectorRateRepository extends JpaRepository<DirectorRate, Long> {
    @Query("SELECT dr FROM DirectorRate dr WHERE dr.director.directorId = :directorId AND dr.user.userId = :userId")
    Optional<DirectorRate> findByDirectorIdAndUserId(@Param(value = "directorId") Long directorId, @Param(value = "userId") Long userId);
    @Modifying
    @Query("DELETE FROM DirectorRate dr WHERE dr.director.directorId = :directorId AND dr.user.userId = :userId")
    int deleteByDirectorIdAndUserId(@Param("directorId") Long directorId, @Param("userId") Long userId);
    @Query("SELECT COALESCE(AVG(dr.rate),0) FROM Director d LEFT JOIN d.directorRates dr WHERE d.directorId = :directorId")
    double getAverageDirectorRate(@Param(value = "directorId") Long directorId);
}

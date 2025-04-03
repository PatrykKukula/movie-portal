package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.MovieRate;

import java.util.Optional;

@Repository
public interface MovieRateRepository extends JpaRepository<MovieRate, Long> {

    @Query("SELECT mr FROM MovieRate mr WHERE mr.movie.movieId = :movieId AND mr.user.userId = :userId")
    Optional<MovieRate> findByMovieIdAndUserId(@Param(value = "movieId") Long movieId, @Param(value = "userId") Long userId);
    @Modifying
    @Query("DELETE FROM MovieRate mr WHERE mr.movie.movieId = :movieId AND mr.user.userId = :userId")
    int deleteByMovieIdAndUserId(@Param("movieId") Long movieId, @Param("userId") Long userId);
}

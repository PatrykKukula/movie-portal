package pl.patrykkukula.MovieReviewPortal.Repository;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.actors WHERE m.movieId = :movieId")
    Optional<Movie> findByIdWithActors(@Param(value = "movieId") Long movieId);
    @Query("SELECT m AS movie, COALESCE(AVG (mr.rate),0) AS rating " +
            "FROM Movie m LEFT JOIN m.actors a LEFT JOIN m.movieRates mr WHERE m.movieId = :movieId GROUP BY m")
    Optional<Tuple> findByIdWithActorsAndMovieRates(@Param(value = "movieId") Long movieId);
    List<Movie> findByTitleContainingIgnoreCaseOrderByTitleAsc(String title);
    List<Movie> findByTitleContainingIgnoreCaseOrderByTitleDesc(String title);
    @Query("SELECT m FROM Movie m ORDER BY m.title ASC")
    List<Movie> findAllOrderByTitleAsc();
    @Query("SELECT m FROM Movie m ORDER BY m.title DESC")
    List<Movie> findAllOrderByTitleDesc();
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates WHERE m.movieId = :movieId")
    Optional<Movie> findByIdWithMovieRates(@Param(value = "movieId") Long movieId);
}

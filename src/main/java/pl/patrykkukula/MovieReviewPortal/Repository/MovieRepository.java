package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.actors WHERE m.movieId = :movieId")
    Optional<Movie> findByIdWithActors(@Param(value = "movieId") Long movieId);
    @EntityGraph(attributePaths = {"actors","director"})
    @Query("SELECT m FROM Movie m WHERE m.movieId = :movieId")
    Optional<Movie> findByIdWithDetails(@Param(value = "movieId") Long movieId);
    List<Movie> findByTitleContainingIgnoreCaseOrderByTitleAsc(String title);
    List<Movie> findByTitleContainingIgnoreCaseOrderByTitleDesc(String title);
    @Query("SELECT m FROM Movie m ORDER BY m.title ASC")
    List<Movie> findAllOrderByTitleAsc();
    @Query("SELECT m FROM Movie m ORDER BY m.title DESC")
    List<Movie> findAllOrderByTitleDesc();
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates WHERE m.movieId = :movieId")
    Optional<Movie> findByIdWithMovieRates(@Param(value = "movieId") Long movieId);
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates ORDER BY m.title ASC")
    List<Movie> findAllWithRatesAsc();
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates ORDER BY m.title DESC")
    List<Movie> findAllWithRatesDesc();
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY m.title ASC")
    List<Movie> findAllWithRatesByTitleAsc(@Value("title") String title);
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY m.title DESC")
    List<Movie> findAllWithRatesByTitleDesc(@Value("title") String title);
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.category= :category ORDER BY m.title ASC")
    List<Movie> findAllWithRatesByTitleAndCategoryAsc(@Value("title") String title, MovieCategory category);
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) AND m.category= :category ORDER BY m.title DESC")
    List<Movie> findAllWithRatesByTitleAndCategoryDesc(@Value("title") String title, MovieCategory category);
    @Query("SELECT SIZE(m.movieRates) FROM Movie m WHERE m.movieId =:movieId")
    Integer countMovieRates(@Param(value = "movieId") Long movieId);
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieRates r ORDER BY r.rate DESC LIMIT 5")
    List<Movie> findTopRatedMovies();
}

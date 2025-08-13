package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import java.util.List;
import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    @Query("SELECT d FROM Director d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT( '%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.firstName ASC, d.lastName ASC")
    List<Director> findAllByFirstOrLastNameSortedAsc(@Param("name") String name);
    @Query("SELECT d FROM Director d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT( '%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.firstName DESC, d.lastName DESC")
    List<Director> findAllByFirstOrLastNameSortedDesc(@Param("name") String name);
    @Query("SELECT d FROM Director d LEFT JOIN FETCH d.directorRates WHERE d.directorId = :directorId")
    Optional<Director> findByIdWithRates(@Param(value = "directorId") Long directorId);
    @Query("SELECT d FROM Director d ORDER BY d.firstName ASC, d.lastName ASC")
    List<Director> findAllSortedAsc();
    @Query("SELECT d FROM Director d ORDER BY d.firstName DESC, d.lastName DESC")
    List<Director> findAllSortedDesc();
    @Query("SELECT d FROM Director d LEFT JOIN FETCH d.movies WHERE d.directorId = :directorId")
    Optional<Director> findByIdWithMovies(@Param(value = "directorId") Long directorId);
    @Query("SELECT SIZE(d.directorRates) FROM Director d WHERE d.directorId =:directorId")
    Integer countDirectorRates(@Param(value = "directorId") Long directorId);
    @Query("SELECT d FROM Director d LEFT JOIN FETCH d.directorRates")
    List<Director> findAllWithDirectorRates(Sort sort);
    @Query("SELECT d FROM Director d LEFT JOIN FETCH d.directorRates WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Director> findAllWithRatesByNameOrLastName(@Value("name") String name, Sort sort);
}

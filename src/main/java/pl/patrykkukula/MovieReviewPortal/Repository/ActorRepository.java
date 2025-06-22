package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Actor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    @Query("SELECT a FROM Actor a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY a.firstName ASC, a.lastName ASC")
    List<Actor> findAllByFirstOrLastNameAsc(@Param(value="name") String name);
    @Query("SELECT a FROM Actor a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY a.firstName DESC, a.lastName DESC")
    List<Actor> findAllByFirstOrLastNameDesc(@Param(value="name") String name);
    Long countByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    @Query("SELECT a FROM Actor a ORDER BY a.firstName ASC, a.lastName ASC")
    List<Actor> findAllSortedByNameAsc();
    @Query("SELECT a FROM Actor a ORDER BY a.firstName DESC, a.lastName DESC")
    List<Actor> findAllSortedByNameDesc();
    @Query("SELECT a FROM Actor a LEFT JOIN FETCH a.movies WHERE a.actorId = :actorId")
    Optional<Actor> findByIdWithMovies(@Param(value = "actorId") Long actorId);
}

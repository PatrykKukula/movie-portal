package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Model.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT DISTINCT u.userId FROM UserEntity u")
    Page<Long> findAllUsersIds(Pageable pageable);
    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT DISTINCT u FROM UserEntity u WHERE u.userId IN :ids")
    List<UserEntity> findAllByIdWithRoles(@Param("ids") List<Long> ids);
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles")
    Page<UserEntity> findAllWithRoles(Pageable pageable);
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<UserEntity> findAllWithRolesByUsername(Pageable pageable, @Param(value = "username") String username);
    @Query("SELECT DISTINCT u.userId FROM UserEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Page<Long> findAllUsersIdsByUsername(Pageable pageable, @Param(value = "username") String username);
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    Integer countUsersByUsername(@Param(value = "username") String username);
    Optional<UserEntity> findByEmail(String email);
    @Query("SELECT DISTINCT u FROM UserEntity u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithRoles(@Param(value = "email") String email);
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.comments WHERE u.userId = :userId")
    Optional<UserEntity> findByIdWithComments(@Param(value = "userId") Long userId);
    @Query("SELECT DISTINCT u FROM UserEntity u WHERE u.username = :username")
    Optional<UserEntity> findByUsername(@Param(value = "username") String username);
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<UserEntity> findByUsernameWithRoles(@Param(value = "username") String username);
    @Query("SELECT u FROM UserEntity  u WHERE u.username = :username OR u.email = :email")
    Optional<UserEntity> findUserByUsernameOrEmail(@Param(value = "username") String username, @Param(value = "email") String email);
    @Query("SELECT AVG(r.rate) FROM UserEntity u LEFT JOIN u.movieRates r WHERE u.userId = :userId")
    Double findAverageMovieRate(@Param(value = "userId") Long userId);
    @Query("SELECT m.category FROM MovieRate r JOIN r.movie m WHERE r.user.userId= :userId GROUP BY m.category ORDER BY COUNT(r) DESC")
    List<MovieCategory> findUserMovieRates(@Param(value = "userId") Long userId);
    @Query("SELECT r FROM MovieRate r LEFT JOIN FETCH r.movie WHERE r.user.userId= :userId ORDER BY r.rate DESC LIMIT 5")
    List<MovieRate> findTopRatedMovies(@Param(value = "userId") Long userId);
    @Query("SELECT r FROM ActorRate r LEFT JOIN r.actor a WHERE r.user.userId= :userId ORDER BY r.rate DESC LIMIT 5")
    List<ActorRate> findTopRatedActors(@Param(value = "userId") Long userId);
    @Query("SELECT r FROM DirectorRate r LEFT JOIN r.director d WHERE r.user.userId= :userId ORDER BY r.rate DESC LIMIT 5")
    List<DirectorRate> findTopRatedDirectors(@Param(value = "userId") Long userId);
    @Query("SELECT COALESCE(COUNT(r),0) FROM MovieRate r WHERE r.user.userId= :userId")
    Long findMovieRateCount(@Param(value = "userId") Long userId);
    @Query("SELECT COALESCE(COUNT(r),0) FROM ActorRate r WHERE r.user.userId= :userId")
    Long findActorRateCount(@Param(value = "userId") Long userId);
    @Query("SELECT COALESCE(COUNT(r),0) FROM DirectorRate r WHERE r.user.userId= :userId")
    Long findDirectorRateCount(@Param(value = "userId") Long userId);
    @Query("SELECT r FROM MovieRate r WHERE r.user.userId= :userId")
    Page<MovieRate> findAllRatedMovies(@Param(value = "userId") Long userId, Pageable pageable);
    @Query("SELECT r FROM ActorRate r WHERE r.user.userId= :userId")
    Page<ActorRate> findAllRatedActors(@Param(value = "userId") Long userId, Pageable pageable);
    @Query("SELECT r FROM DirectorRate r WHERE r.user.userId= :userId")
    Page<DirectorRate> findAllRatedDirectors(@Param(value = "userId") Long userId, Pageable pageable);
}

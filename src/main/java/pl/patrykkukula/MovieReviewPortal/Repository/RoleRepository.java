package pl.patrykkukula.MovieReviewPortal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByRoleName(String roleName);
    @Query("SELECT r From Role r LEFT JOIN FETCH r.users")
    Optional<Role> findRoleByRoleNameWithUsers(String roleName);
}

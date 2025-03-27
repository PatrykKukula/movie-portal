package pl.patrykkukula.MovieReviewPortal.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

}

package pl.patrykkukula.MovieReviewPortal.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c INNER JOIN c.topic t WHERE t.topicId = :topicId ORDER BY c.commentIdInPost ASC")
    List<Comment> findAllByTopicIdSortedAsc(@Param(value = "topicId") Long topicId);
    @Query("SELECT c FROM Comment c INNER JOIN c.topic t WHERE t.topicId = :topicId ORDER BY c.commentIdInPost DESC")
    List<Comment> findAllByTopicIdSortedDesc(@Param(value = "topicId") Long topicId);
    @Query("SELECT c FROM Comment c INNER JOIN c.topic ORDER BY c.commentId ASC")
    List<Comment> findAllWithTopicOrderByIdAsc();
    @Query("SELECT c FROM Comment c  INNER JOIN c.topic ORDER BY c.commentId DESC")
    List<Comment> findAllWithTopicOrderByIdDesc();
    @Query("SELECT c FROM Comment c WHERE c.user.username = :username")
    List<Comment> findByUsername(@Param(value = "username") String username);
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.commentId = :commentId")
    Optional<Comment> findCommentByIdWithUser(@Param(value = "commentId") Long commentId);
}

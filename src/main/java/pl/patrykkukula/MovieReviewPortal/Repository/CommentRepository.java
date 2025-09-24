package pl.patrykkukula.MovieReviewPortal.Repository;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user JOIN FETCH c.topic WHERE c.commentId = :commentId")
    Optional<Comment> findCommentByIdWithUserAndTopic(@Param(value = "commentId") Long commentId);
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.commentId = :commentId")
    Optional<Comment> findCommentByIdWithUser(@Param(value = "commentId") Long commentId);
    @Query("SELECT c FROM Comment c WHERE c.isReply= true AND c.repliedCommentId= :commentId ORDER BY c.createdAt DESC")
    List<Comment> findAllRepliesByCommentId(@Param(value = "commentId") Long commentId);
    @Query("SELECT DISTINCT c FROM Comment c JOIN FETCH c.user WHERE c.topic.topicId= :topicId")
    List<Comment> findAllCommentsForTopicWithUsers(@Param(value = "topicId") Long topicId);
    @Query("SELECT c.user.userId as userId, COUNT(c) as count FROM Comment c WHERE c.topic.topicId= :topicId GROUP BY c.user.userId")
    List<Tuple> countCommentsForUserByTopicId(@Param(value = "topicId") Long topicId);
    @Query("SELECT c FROM Comment c JOIN FETCH c.user u WHERE u.username= :username ORDER BY c.createdAt DESC")
    List<Comment> findAllCommentsForUserByUsername(@Param(value = "username") String username);
    @Query("SELECT c FROM Comment c JOIN FETCH c.topic t JOIN c.user u WHERE u.username= :username ORDER BY c.createdAt DESC")
    List<Comment> findAllCommentsForUserByUsernameFetchTopic(@Param(value = "username") String username);
}

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

    @Query("SELECT c FROM Comment c WHERE c.topic.topicId = :topicId ORDER BY c.commentIdInPost ASC")
    List<Comment> findAllByTopicIdSortedAsc(@Param(value = "topicId") Long topicId);
    @Query("SELECT c FROM Comment c WHERE c.topic.topicId = :topicId ORDER BY c.commentIdInPost DESC")
    List<Comment> findAllByTopicIdSortedDesc(@Param(value = "topicId") Long topicId);
    @Query("SELECT c FROM Comment c ORDER BY c.commentId ASC")
    List<Comment> findAllOrderByIdAsc();
    @Query("SELECT c FROM Comment c ORDER BY c.commentId DESC")
    List<Comment> findAllOrderByIdDesc();
    @Query("SELECT c FROM Comment c LEFT JOIN c.user WHERE c.commentId = :commentId")
    Optional<Comment> findCommentByIdWithUser(@Param(value = "commentId") Long commentId);
}

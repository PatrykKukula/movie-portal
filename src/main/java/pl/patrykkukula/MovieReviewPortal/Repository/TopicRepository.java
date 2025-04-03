package pl.patrykkukula.MovieReviewPortal.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Dto.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t AS topic, COALESCE(MAX(c.commentIdInPost), 0) AS maxId FROM Topic t INNER JOIN t.comments c WHERE t.topicId = :topicId GROUP BY t")
    Optional<Tuple> findTopicWithCurrentMaxCommentId(@Param(value = "topicId") Long topicId);
    @Query("SELECT t FROM Topic t JOIN FETCH t.comments WHERE t.topicId = :topicId")
    Optional<Topic> findByTopicIdWithComments(Long topicId);
    @Query("SELECT t FROM Topic t ORDER BY t.topicId ASC")
    List<Topic> findAllOrderByTopicIdAsc();
    @Query("SELECT t FROM Topic t ORDER BY t.topicId DESC")
    List<Topic> findAllOrderByTopicIdDesc();
    List<Topic> findByTitleContainingIgnoreCaseOrderByTitleAsc(String title);
    List<Topic> findByTitleContainingIgnoreCaseOrderByTitleDesc(String title);
}
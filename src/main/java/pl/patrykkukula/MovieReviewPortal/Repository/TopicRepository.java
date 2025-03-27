package pl.patrykkukula.MovieReviewPortal.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t AS topic, COALESCE(MAX(c.commentIdInPost), 0) AS maxId FROM Topic t INNER JOIN t.comments c WHERE t.topicId = :topicId")
    Optional<Tuple> fetchTopicWithCurrentMaxCommentId(@Param(value = "topicId") Long topicId);

}

package pl.patrykkukula.MovieReviewPortal.Mapper;

import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class CommentMapper {

    public static CommentDtoWithUser mapToCommentDtoWithUser(Comment comment, Map<Long, Long> commentsCount) {
        String registeredAt = comment.getUser().getRegisteredAt() != null ?
                comment.getUser().getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
        String createdAt = comment.getCreatedAt() != null ?
                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm")) : "";
        String updatedAt = comment.getUpdatedAt() != null ?
                comment.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm")) : "";

        long commentCount = commentsCount.getOrDefault(comment.getUser().getUserId(), 0L);
        return CommentDtoWithUser.builder()
                        .commentId(comment.getCommentId())
                        .text(comment.getText())
                        .author(comment.getUser().getUsername())
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .commentIdInPost(comment.getCommentIdInPost())
                        .userRegistered(registeredAt)
                        .userCommentCount(commentCount)
                        .userId(comment.getUser().getUserId())
                        .isReply(comment.isReply())
                        .repliedCommentId(comment.getRepliedCommentId())
                        .topicId(comment.getTopic().getTopicId())
                        .build();
    }
}

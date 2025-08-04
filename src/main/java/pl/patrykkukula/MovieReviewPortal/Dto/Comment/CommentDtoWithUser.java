package pl.patrykkukula.MovieReviewPortal.Dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDtoWithUser {
    private Long commentId;
    private String text;
    private String createdAt;
    private String updatedAt;
    private String author;
    private Long commentIdInPost;
    private String userRegistered;
    private Long userCommentCount;
    private boolean isReply;
    private Long repliedCommentId;
    private Long userId;
    private Long topicId;
}

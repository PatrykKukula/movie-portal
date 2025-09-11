package pl.patrykkukula.MovieReviewPortal.Dto.Comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CommentDtoWithReplies {
    private Long commentId;
    private String text;
    private String createdAt;
    private String updatedAt;
    private String author;
    @JsonProperty(index = 1)
    private boolean isReply;
    private Long repliedCommentId;
    private Long topicId;
    private List<CommentDtoWithUser> replies;
}


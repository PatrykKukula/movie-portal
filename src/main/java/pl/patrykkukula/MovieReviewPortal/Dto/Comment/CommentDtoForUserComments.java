package pl.patrykkukula.MovieReviewPortal.Dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentDtoForUserComments {
    private String text;
    private Long topicId;
    private String topicTitle;
    private String createdAt;
    private String updatedAt;
}

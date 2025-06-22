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

    private String text;
    private Long topicId;
    private Long commentIdInPost;
    private String user;
}

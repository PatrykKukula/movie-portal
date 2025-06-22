package pl.patrykkukula.MovieReviewPortal.Dto.Topic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDtoWithCommentDto {
    @NotNull(message = "Topic cannot be empty")
    private TopicDto topic;
    @NotNull(message = "Comment cannot be empty")
    @Valid
    private CommentDto comment;
}

package pl.patrykkukula.MovieReviewPortal.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDtoWithCommentDto {
    @NotNull(message = "Topic cannot be null")
    @Valid
    private TopicDto topic;
    @NotNull(message = "Comment cannot be null")
    @Valid
    private CommentDto comment;
}

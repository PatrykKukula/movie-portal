package pl.patrykkukula.MovieReviewPortal.Dto.Comment;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CommentDto {
    @NotEmpty(message = "Text cannot be empty")
    @Size(max = 1000, message = "Title must not exceed 1000 characters")
    private String text;
    @Positive(message = "Topic ID must be greater than 0")
    private Long topicId;
    private Long commentIdInPost;
}

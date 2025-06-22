package pl.patrykkukula.MovieReviewPortal.Dto.Topic;

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
public class TopicDto {

    @NotEmpty(message = "Title cannot empty")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    @Positive(message = "Movie id cannot be less than 0")
    private Long movieId;
}

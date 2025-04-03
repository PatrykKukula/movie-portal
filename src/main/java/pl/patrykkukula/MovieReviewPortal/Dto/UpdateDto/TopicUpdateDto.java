package pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicUpdateDto {

    @NotEmpty(message = "Title cannot be null or empty")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
}

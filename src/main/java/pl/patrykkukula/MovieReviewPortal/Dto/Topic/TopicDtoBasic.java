package pl.patrykkukula.MovieReviewPortal.Dto.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDtoBasic {
    private Long id;
    private String title;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long postCount;
}

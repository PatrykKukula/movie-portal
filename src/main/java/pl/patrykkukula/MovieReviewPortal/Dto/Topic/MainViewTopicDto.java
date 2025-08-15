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
public class MainViewTopicDto {
    private Long id;
    private String title;
    private String createdBy;
    private Long userId;
    private LocalDateTime createdAt;
    private long commentCount;
    private Long entityId;
    private String entityName;
    private String entityType;
}


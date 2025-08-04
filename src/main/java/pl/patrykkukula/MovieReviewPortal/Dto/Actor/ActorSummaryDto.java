package pl.patrykkukula.MovieReviewPortal.Dto.Actor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ActorSummaryDto {
    private Long id;
    private String fullName;
}

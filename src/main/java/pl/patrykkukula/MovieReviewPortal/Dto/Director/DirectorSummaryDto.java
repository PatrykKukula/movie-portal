package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class DirectorSummaryDto {
    private Long id;
    private String fullName;
}

package pl.patrykkukula.MovieReviewPortal.Dto.Movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDtoBasic {
    private Long id;
    private String title;
    private String category;
}

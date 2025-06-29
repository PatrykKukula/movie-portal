package pl.patrykkukula.MovieReviewPortal.Dto.Movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class MovieViewDto {
    private Long id;
    private String title;
    private Double rating;
    private Integer rateNumber;
    private LocalDate releaseDate;
    private MovieCategory category;
}

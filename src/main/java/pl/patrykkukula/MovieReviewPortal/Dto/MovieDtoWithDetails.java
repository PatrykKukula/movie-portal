package pl.patrykkukula.MovieReviewPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDtoWithDetails {
    private Long id;
    private String title;
    private String description;
    private Double rating;
    private LocalDate releaseDate;
    private String category;
    private String director;
    private List<String> actors;

}

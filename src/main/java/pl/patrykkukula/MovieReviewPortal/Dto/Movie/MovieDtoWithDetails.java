package pl.patrykkukula.MovieReviewPortal.Dto.Movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;

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
    private Integer rateNumber;
    private LocalDate releaseDate;
    private String category;
    private DirectorDto director;
    private List<ActorDto> actors;
}

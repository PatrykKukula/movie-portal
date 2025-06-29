package pl.patrykkukula.MovieReviewPortal.Dto.Movie;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorSummaryDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    @NotEmpty(message = "Movie title cannot be empty")
    @Size(max = 255, message = "Movie title cannot exceed 255 characters")
    private String title;
    @Size(max = 1000, message = "Movie description cannot exceed 1000 characters")
    private String description;
    @NotNull(message = "Release date is required")
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;
    @NotNull(message = "Category is required")
    private MovieCategory category;
    private Long directorId;
    private List<Long> actorIds = new ArrayList<>();
}

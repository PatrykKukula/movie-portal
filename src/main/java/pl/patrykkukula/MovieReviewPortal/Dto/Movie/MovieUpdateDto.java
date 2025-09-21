package pl.patrykkukula.MovieReviewPortal.Dto.Movie;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieUpdateDto {
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;
    private MovieCategory category;
    @Positive(message = "Director ID cannot less than 1")
    private Long directorId;
}

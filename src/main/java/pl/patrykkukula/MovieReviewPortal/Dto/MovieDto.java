package pl.patrykkukula.MovieReviewPortal.Dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    @NotEmpty(message = "Movie title cannot be null or empty")
    @Size(max = 255, message = "Movie title cannot exceed 255 characters")
    private String title;
    @Size(max = 1024, message = "Movie description cannot exceed 1024 characters")
    private String description;
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;
    private String category;
    private Long directorId;
}

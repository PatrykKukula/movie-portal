package pl.patrykkukula.MovieReviewPortal.Dto.UpdateDto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieUpdateDto {
    @Size(max = 255, message = "Movie title cannot exceed 255 characters")
    private String title;
    @Size(max = 1024, message = "Movie description cannot exceed 1024 characters")
    private String description;
    @PastOrPresent(message = "Release date cannot be in the past")
    private LocalDate releaseDate;
    private String category;
    private Long directorId;
}

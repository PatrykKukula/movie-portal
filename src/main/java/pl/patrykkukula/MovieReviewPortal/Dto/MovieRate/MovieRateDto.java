package pl.patrykkukula.MovieReviewPortal.Dto.MovieRate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class MovieRateDto {
    @Min(value = 1, message = "Rate must be between 1 and 6")
    @Max(value = 6, message = "Rate must be between 1 and 6")
    private Integer rate;
    @Positive(message = "Movie id cannot be less than 1")
    private Long movieId;
    private UserEntity user;
}

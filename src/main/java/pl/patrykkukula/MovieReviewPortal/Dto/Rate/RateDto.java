package pl.patrykkukula.MovieReviewPortal.Dto.Rate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class RateDto {
    @Min(value = 1, message = "Rate must be between 1 and 6")
    @Max(value = 6, message = "Rate must be between 1 and 6")
    private Integer rate;
    @Positive(message = "Id cannot be less than 1")
    private Long entityId;
}

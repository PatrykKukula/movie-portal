package pl.patrykkukula.MovieReviewPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ErrorResponseDto {

    private String path;
    private String statusCode;
    private String statusMessage;
    private String errorMessage;
    private LocalDateTime occurrenceTime;
}

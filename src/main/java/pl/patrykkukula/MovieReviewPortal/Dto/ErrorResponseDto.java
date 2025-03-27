package pl.patrykkukula.MovieReviewPortal.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {

    private String path;
    private String statusCode;
    private String statusMessage;
    private String errorMessage;
    private LocalDateTime occurrenceTime;
}

package pl.patrykkukula.MovieReviewPortal.Dto;

import lombok.*;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ResponseDto {

    private String statusCode;
    private String statusMessage;
}

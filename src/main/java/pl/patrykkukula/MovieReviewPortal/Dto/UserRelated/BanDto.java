package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BanDto {
    private String username;
    private String banDuration;
}

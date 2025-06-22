package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class UserEntityDisplayDto {

    private String username;
    private String email;
}

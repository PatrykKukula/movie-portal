package pl.patrykkukula.MovieReviewPortal.Dto.UserRelated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class UserDataDto {
    private Long userId;
    private String username;
    private String email;
    private String status;
    private String banExpirationDate;
    private List<String> roles;
}

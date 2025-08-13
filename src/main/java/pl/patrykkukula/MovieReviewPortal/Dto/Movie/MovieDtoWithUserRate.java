package pl.patrykkukula.MovieReviewPortal.Dto.Movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;

@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class MovieDtoWithUserRate implements EntityWithRate {
    private Long id;
    private String title;
    private Integer userRate;

    @Override
    public Long getId() {
        return id;
    }
    @Override
    public String getText() {
        return title;
    }
    @Override
    public String getType() {
        return "Movie";
    }
    @Override
    public Integer getUserRate() {
        return userRate;
    }
}

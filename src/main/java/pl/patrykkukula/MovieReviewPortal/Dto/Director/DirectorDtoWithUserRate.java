package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectorDtoWithUserRate implements EntityWithRate {
    private Long id;
    private String firstName;
    private String lastName;
    @JsonIgnore
    private Integer userRate;
    private Double averageRate;

    @Override
    public Long getId() {
        return id;
    }
    @Override
    public String getText() {
        return firstName + " " + lastName;
    }
    @Override
    public String getType() {
        return "Director";
    }
    @Override
    public Integer getUserRate() {
        return userRate;
    }
    @Override
    public Double getAverageRate() {
        return averageRate;
    }
}

package pl.patrykkukula.MovieReviewPortal.Dto.Actor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActorDtoWithUserRate implements EntityWithRate {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer userRate;

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
        return "Actor";
    }
    @Override
    public Integer getUserRate() {
        return userRate;
    }
}

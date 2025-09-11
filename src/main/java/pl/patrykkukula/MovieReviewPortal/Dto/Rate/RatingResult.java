package pl.patrykkukula.MovieReviewPortal.Dto.Rate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record RatingResult(Double avgRate, boolean wasRated) {
    @JsonIgnore
    public boolean getWasRated(){
        return wasRated;
    }
    public Double getRate(){
        return avgRate;
    }
}

package pl.patrykkukula.MovieReviewPortal.Dto;

public interface EntityWithRate {
    Long getId();
    String getText();
    String getType();
    Integer getUserRate();
}

package pl.patrykkukula.MovieReviewPortal.Dto;

import java.time.LocalDate;

public interface ViewableEntity {
    Long getId();
    String getFirstName();
    String getLastName();
    String getCountry();
    LocalDate getDateOfBirth();
    Double getAverageRate();
    Integer getRateNumber();
}

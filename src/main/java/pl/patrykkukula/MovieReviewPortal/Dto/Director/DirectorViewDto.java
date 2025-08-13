package pl.patrykkukula.MovieReviewPortal.Dto.Director;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.patrykkukula.MovieReviewPortal.Dto.ViewableEntity;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectorViewDto implements ViewableEntity {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
    private Double averageRate;
    private Integer rateNumber;

    @Override
    public Long getId(){
        return id;
    }
    @Override
    public String getFirstName(){
        return firstName;
    }
    @Override
    public String getLastName(){
        return lastName;
    }
    @Override
    public String getCountry(){
        return country;
    }
    @Override
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    @Override
    public Double getAverageRate() {
        return averageRate;
    }
    @Override
    public Integer getRateNumber(){
        return rateNumber;
    }

}

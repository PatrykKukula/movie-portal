package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;

import java.util.function.BiFunction;
import java.util.function.Function;

public class RatingStarsLayout extends Div{
    public  RatingStarsLayout (Double rating, Integer rateNumber, UserDetailsServiceImpl userDetailsService, Long entityId,
                                 RateDto rateDto, BiFunction<Integer, Long, RatingResult> getRating, Function<Long, Double> getNewRate) {
            Span avgSpan = new Span(String.format("%.2f", rating));
            Span labelText = new Span("Rating: ");
            labelText.getStyle().set("font-weight", "bold");
            Div ratingDiv = new Div(labelText, avgSpan);
            ratingDiv.getStyle().set("display", "inline");

            Span initRateNumber = new Span(String.valueOf(rateNumber));
            initRateNumber.getElement().getThemeList().add("badge pill small contrast");
            initRateNumber.getStyle().set("margin-inline-start", "var(--lumo-space-s)");

            Long userId = userDetailsService.getAuthenticatedUserId();
            RatingStars ratingStars = new RatingStars(
                    rateDto != null ? rateDto.getRate() : -1,
                    userId == null,
                    newRate -> {
                        RatingResult newAvg = getRating.apply(newRate, entityId);
                        avgSpan.setText(String.format("%.2f", newAvg.avgRate()));
                        if (!newAvg.wasRated()) initRateNumber.setText(String.valueOf(Integer.parseInt(initRateNumber.getText())+1));
                    },
                    () -> {
                        Double newRate = getNewRate.apply(entityId);
                        avgSpan.setText(String.format("%.2f", newRate));
                        initRateNumber.setText(String.valueOf(Integer.parseInt(initRateNumber.getText())-1));
                        return newRate == null;
                    }
            );
            ratingDiv.add(initRateNumber, ratingStars);
            add(ratingDiv);
        }
}


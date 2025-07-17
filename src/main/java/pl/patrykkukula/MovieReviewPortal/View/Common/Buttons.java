package pl.patrykkukula.MovieReviewPortal.View.Common;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.HasUrlParameter;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.RatingStars;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Buttons {

    private Buttons (){}

    public static <C extends Component> Button cancelButton(Class<? extends C> navigationTarget) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return cancelButton;
    }
    public static <T, C extends Component & HasUrlParameter<T>> Button cancelButton(Class<? extends C> navigationTarget, T parameter) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget, parameter));
        return cancelButton;
    }
    public static Button cancelButton(Dialog dialog) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> dialog.close());
        return cancelButton;
    }
    public static <T, C extends Component & HasUrlParameter<T>> Button editButton(Class<? extends C> navigationTarget, String label, T parameter) {
        Button cancelButton = new Button(label);
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget, parameter));
        return cancelButton;
    }
    public static <C extends Component> Button backButton(Class<? extends C> navigationTarget, String label) {
        Button cancelButton = new Button(label);
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return cancelButton;
    }
    public static <C extends Component> Button addButton(Class<? extends C> navigationTarget, String label) {
        Button addButton = new Button(label);
        addButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        addButton.setPrefixComponent(VaadinIcon.PLUS.create());
        return addButton;
    }

    public static class RatingStarsLayout extends Div {
        public RatingStarsLayout(Double rating, Integer rateNumber, UserDetailsServiceImpl userDetailsService, Long entityId,
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
}

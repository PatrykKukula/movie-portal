package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class RatingStars extends Div {
    private final List<Icon> stars = new ArrayList<>();
    private int initialRate;
    private boolean readOnly;
    private AtomicInteger currentRate = new AtomicInteger();
    private Div loginText = new Div("Log in to rate");
    private static final int MAX_STARS = 6;
    private static final String COLOR_ACTIVE = "yellow";
    private static final String COLOR_INACTIVE = "lightgray";

    public RatingStars(int initialRate, boolean readOnly, IntConsumer onRateChange, Supplier<Boolean> removeRate) {
        this.initialRate = initialRate;
        this.readOnly = readOnly;

        loginText.getStyle().set("font-size", "9px").set("display", "none").set("padding-left", "5px");
        currentRate.set(initialRate);
        for (int i = 1; i<=MAX_STARS; i++){
            Icon star = createStarIcon(i, readOnly, onRateChange, removeRate);
            add(star, loginText);
        }
    }
    private Icon createStarIcon(int index, boolean readOnly, IntConsumer onRateChange, Supplier<Boolean> removeRate){
        Icon star = VaadinIcon.STAR.create();
        star.setClassName("icon-star");
        star.getElement().setAttribute("data-index", String.valueOf(index));
        stars.add(star);

        if(index <= currentRate.get()) star.getStyle().set("color", COLOR_ACTIVE);
        addHoverListeners(index, currentRate, readOnly);
        if (!readOnly) {
            star.addClickListener(e -> {
                if (index == currentRate.get()) {
                    removeRate.get();
                    currentRate.set(0);
                }
                else {
                    currentRate.set(index);
                    onRateChange.accept(currentRate.get());
                }
                updateStars(currentRate.get());
            });
        }
        else star.getStyle().set("cursor", "default");

        return star;
    }
    private void addHoverListeners(int hoverIndex, AtomicInteger currentRate, boolean readOnly) {
        stars.get(hoverIndex - 1).getElement().addEventListener("mouseover", e -> {
            updateStars(hoverIndex);
            if (readOnly) {
                loginText.getStyle().set("display", "inline");
            }
        });
        stars.get(hoverIndex - 1).getElement().addEventListener("mouseout", e -> {
            updateStars(currentRate.get());
            if (readOnly) {
                loginText.getStyle().set("display", "none");
            }
        });
    }
    private void updateStars(int currentRate){
        for (int i = 0; i < MAX_STARS; i++) {
            Icon star = stars.get(i);
            if (i <= currentRate-1) {
                star.getStyle().set("color", COLOR_ACTIVE);
            } else {
                star.getStyle().set("color", COLOR_INACTIVE);
            }
        }
    }
}

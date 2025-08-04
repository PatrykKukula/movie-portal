package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;

import java.util.List;

public class MoviesDiv extends Div {
    public MoviesDiv(List<MovieDtoBasic> movies) {
        addClassName("movies");
        Span moviesSpan = CommonComponents.labelSpan("Movies");
        add(moviesSpan);
        for (MovieDtoBasic movie : movies) {
            Anchor movieLink = new Anchor("movies/" + movie.getId(), "• " + movie.getTitle());
            add(movieLink);
        }
    }
}

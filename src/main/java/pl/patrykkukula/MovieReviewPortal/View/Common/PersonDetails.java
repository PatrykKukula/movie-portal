package pl.patrykkukula.MovieReviewPortal.View.Common;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;

import java.util.List;

public class PersonDetails extends VerticalLayout {
    public PersonDetails(String headerSup, String countrySup, String dateSup, String biographySup, List<MovieDtoBasic> moviesSup) {
        setPadding(false);
        setMargin(false);

        H3 header = new H3(headerSup);
        Span country = createDetail("Country: ", countrySup);
        Span dateOfBirth = createDetail("Date of birth ", dateSup);
        Span biography = createDetail("Biography: ", biographySup);

        Div movies = new Div();
        movies.addClassName("movies");
        Span moviesSpan = CommonComponents.labelSpan("Movies");
        movies.add(moviesSpan);
        for(MovieDtoBasic movie : moviesSup) {
            Anchor movieLink = new Anchor("movies/" + movie.getId(), "• " + movie.getTitle());
            movies.add(movieLink);
        }

        add(header, country, dateOfBirth, biography, movies);
    }
    private Span createDetail(String label, String value){
        Span detail = new Span(CommonComponents.labelSpan(label));
        detail.add(value);
        return detail;
    }
}

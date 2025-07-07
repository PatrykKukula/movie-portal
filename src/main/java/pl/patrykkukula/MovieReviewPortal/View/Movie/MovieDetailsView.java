package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.RatingStars;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

@Slf4j
@Route("movies")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class MovieDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final MovieServiceImpl movieService;
    private final UserDetailsServiceImpl userDetailsService;

    public MovieDetailsView(MovieServiceImpl movieService, UserDetailsServiceImpl userDetailsService) {
        this.movieService = movieService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long movieId) {
        setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName("main-layout");

        VerticalLayout rightSideLayout = new VerticalLayout();
//        rightSideLayout.setWidth("20%");
        try {
            MovieDtoWithDetails movie = movieService.fetchMovieDetailsById(movieId);

            VerticalLayout detailsLayout = new VerticalLayout();
            H3 header = new H3(movie.getTitle());

            Div directorDiv = setDirector(movie);
            Div category = new Div("Category: " + movie.getCategory());
            Div releaseDate = new Div("Release date: " + movie.getReleaseDate().toString());
            Div description = new Div("Description: " + movie.getDescription());

            Span avgSpan = new Span(String.format("%.2f", movie.getRating()));
            Div rating = new Div(new Text("Rating: "),avgSpan);
            rating.getStyle().set("display", "inline");

            Span initRateNumber = new Span(String.valueOf(movie.getRateNumber()));
            initRateNumber.getElement().getThemeList().add("badge pill small contrast");
            initRateNumber.getStyle().set("margin-inline-start", "var(--lumo-space-s)");

            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto movieRateDto = movieService.fetchMovieRateByMovieIdAndUserId(movieId, userId);
            RatingStars ratingStars = new RatingStars(
                    movieRateDto != null ? movieRateDto.getRate() : -1,
                    userId == null,
                    newRate -> {
                        RatingResult newAvg = movieService.addRateToMovie(new RateDto(newRate, movieId));
                        avgSpan.setText(String.format("%.2f", newAvg.avgRate()));
                        if (!newAvg.wasRated()) initRateNumber.setText(String.valueOf(Integer.parseInt(initRateNumber.getText())+1));
                    },
                    () -> {
                        Double newRate = movieService.removeRate(movieId);
                        avgSpan.setText(String.format("%.2f", newRate));
                        initRateNumber.setText(String.valueOf(Integer.parseInt(initRateNumber.getText())-1));
                        return newRate == null;
                    }
            );
            rating.add(initRateNumber, ratingStars);

            UI.getCurrent().getPage().setTitle("movie"); // DOES NOT WORK - FIGURE IT OUT

            Button editButton = new Button("Edit", e -> UI.getCurrent().navigate(MovieEditView.class, movieId));
            Button deleteButton = new Button("Delete", e -> confirmDelete(movieId));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button backButton = new Button("Back to Movies", e -> UI.getCurrent().navigate(MovieView.class));

            VerticalLayout buttonsLayout = new VerticalLayout();
            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.add(backButton);
            buttonsLayout.addClassName("buttons-layout");

            H5 actorsHeader = new H5("Actors");
            Div actors = getActors(movie);

            detailsLayout.setClassName("details-layout");
            detailsLayout.add(header, category, releaseDate, description, rating, directorDiv, actorsHeader, actors, buttonsLayout);

            mainLayout.add(detailsLayout, rightSideLayout);

            add(mainLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
    private void confirmDelete(Long movieId){
        Dialog dialog = new Dialog();
        var text = new Span("Do you want to remove Movie? This action cannot be undone.");

        Button confirmButton = new Button("Confirm", e -> {
            movieService.deleteMovie(movieId);
            dialog.close();
            UI.getCurrent().navigate(MovieView.class);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = Buttons.cancelButton(dialog);

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.getStyle().set("padding-top", "25px");

        dialog.add(text, buttons);
        dialog.open();
    }
    private static Div setDirector(MovieDtoWithDetails movie) {
        Div directorDiv = new Div();
        Span directorHeader = new Span("Director: ");
        directorDiv.add(directorHeader);
        if (movie.getDirector() != null && movie.getDirector().getFirstName() != null && movie.getDirector().getLastName() != null) {
            DirectorDto director = movie.getDirector();
            Anchor directorAnchor = new Anchor("directors/" + director.getId(), director.getFirstName() + " " + director.getLastName());
            directorDiv.add(directorAnchor);
        }
        return directorDiv;
    }
    private Div getActors(MovieDtoWithDetails movie) {
        Div actors = new Div();
        actors.getStyle().set("white-space", "pre-line");
        movie.getActors()
                .forEach( actor -> {
                    var actorLink = new Anchor("actors/" + actor.getId(),"• " + actor.getFirstName() + " " + actor.getLastName() + "\n");
                    actors.add(actorLink);
                }
                );
        return actors;
    }
}

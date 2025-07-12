package pl.patrykkukula.MovieReviewPortal.View.Movie;

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
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.RatingStarsLayout;
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
        try {
            MovieDtoWithDetails movie = movieService.fetchMovieDetailsById(movieId);
            H3 header = new H3(movie.getTitle());

            VerticalLayout detailsLayout = detailsLayout(movie);

            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = movieService.fetchRateByMovieIdAndUserId(movieId, userId);
            RatingStarsLayout ratingStarsLayout = new RatingStarsLayout(
                    movie.getRating(),
                    movie.getRateNumber(),
                    userDetailsService,
                    movieId,
                    rateDto,
                    (newRate, entityId) -> movieService.addRateToMovie(new RateDto(newRate, entityId)),
                    entityId -> movieService.removeRate(entityId)
            );

            UI.getCurrent().getPage().setTitle("movie"); // DOES NOT WORK - FIGURE IT OUT

            Button editButton = new Button("Edit movie", e -> UI.getCurrent().navigate(MovieEditView.class, movieId));
            Button deleteButton = new Button("Delete movie", e -> confirmDelete(movieId));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button backButton = new Button("Back to movies", e -> UI.getCurrent().navigate(MovieView.class));

            VerticalLayout buttonsLayout = new VerticalLayout();
            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.add(backButton);
            buttonsLayout.addClassName("buttons-layout");

            Span actorsLabel = new Span("Actors");
            actorsLabel.getStyle().set("font-weight", "bold");
            Div actors = getActors(movie);

            VerticalLayout layout = new VerticalLayout();
            layout.addClassName("details-layout");
            layout.add(header, detailsLayout, ratingStarsLayout, actorsLabel, actors, buttonsLayout);

            mainLayout.add(layout, rightSideLayout);

            add(mainLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
    private void confirmDelete(Long movieId){
        Dialog dialog = new Dialog();
        var text = new Span("Do you want to remove Movie? This action cannot be undone.");

        Button confirmButton = new Button("Confirm delete", e -> {
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
        Span directorLabel = CommonComponents.labelSpan("Director: ");
        directorDiv.add(directorLabel);
        if (movie.getDirector() != null && movie.getDirector().getFirstName() != null && movie.getDirector().getLastName() != null) {
            DirectorDto director = movie.getDirector();
            Anchor directorAnchor = new Anchor("directors/" + director.getId(), director.getFirstName() + " " + director.getLastName());
            directorDiv.add(directorAnchor);
        }
        return directorDiv;
    }
    private VerticalLayout detailsLayout(MovieDtoWithDetails movie){
        Div directorSpan = setDirector(movie);
        Span categoryLabel = CommonComponents.labelSpan("Category: ");
        Span categorySpan = new Span(categoryLabel);
        categorySpan.add(movie.getCategory());
        Span releaseDateLabel = CommonComponents.labelSpan("Release date: ");
        Span releaseDateSpan = new Span(releaseDateLabel);
        releaseDateSpan.add(movie.getReleaseDate().toString());
        Span descriptionLabel = CommonComponents.labelSpan("Description: ");
        Span descriptionDiv = new Span(descriptionLabel);
        descriptionDiv.add(movie.getDescription());

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(false);
        detailsLayout.add(directorSpan, categorySpan, releaseDateSpan, descriptionDiv);
        return detailsLayout;
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

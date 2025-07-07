package pl.patrykkukula.MovieReviewPortal.View.Actor;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RatingResult;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.RatingStars;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

import java.util.List;

@Route("actors")
@PageTitle("Actor")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class ActorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ActorServiceImpl actorService;
    private final UserDetailsServiceImpl userDetailsService;

    public ActorDetailsView(ActorServiceImpl actorService, UserDetailsServiceImpl userDetailsService) {
        this.actorService = actorService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long actorId) {
        setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName("main-layout");
        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setWidth("35%");
        try {
            ActorDtoWithMovies actor = actorService.fetchActorByIdWithMovies(actorId);

            VerticalLayout detailsLayout = new VerticalLayout();
            H3 header = new H3(actor.getFirstName() + " " + actor.getLastName());
            List<MovieDtoBasic> movies = actor.getMovies();

            Div country = new Div(actor.getCountry());
            Div dateOfBirth = new Div(actor.getDateOfBirth().toString());
            Div biography = new Div(actor.getBiography());

            Div actorDetails = new Div();
            actorDetails.addClassName("details");
            actorDetails.setText("Country: " + country.getText() + "\nDate of birth: " + dateOfBirth.getText()
                    + "\nBiography: " + biography.getText());

            Div actorMovies = new Div();
            actorMovies.setClassName("movies");
            actorMovies.add(new H5("Movies"));

            Span avgSpan = new Span(String.format("%.2f", actor.getRating()));
            Div rating = new Div(new Text("Rating: "),avgSpan);
            rating.getStyle().set("display", "inline");

            Span initRateNumber = new Span(String.valueOf(actor.getRateNumber()));
            initRateNumber.getElement().getThemeList().add("badge pill small contrast");
            initRateNumber.getStyle().set("margin-inline-start", "var(--lumo-space-s)");

            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = actorService.fetchRateByActorIdAndUserId(actorId, userId);
            RatingStars ratingStars = new RatingStars(
                    rateDto != null ? rateDto.getRate() : -1,
                    userId == null,
                    newRate -> {
                        RatingResult newAvg = actorService.addRateToActor(new RateDto(newRate, actorId));
                        avgSpan.setText(String.format("%.2f", newAvg.avgRate()));
                        if (!newAvg.wasRated()) initRateNumber.setText(String.valueOf(Integer.parseInt(initRateNumber.getText())+1));
                    },
                    () -> {
                        Double newRate = actorService.removeRate(actorId);
                        avgSpan.setText(String.format("%.2f", newRate));
                        initRateNumber.setText(String.valueOf(Integer.parseInt(initRateNumber.getText())-1));
                        return newRate == null;
                    }
            );
            rating.add(initRateNumber, ratingStars);

            for(MovieDtoBasic movie : movies) {
                Anchor movieLink = new Anchor("movies/" + movie.getId(), "• " + movie.getTitle());
                movieLink.addClassName("details");
                actorMovies.add(movieLink);
            }

            Button editButton = Buttons.editButton(ActorEditView.class, actorId);
            Button deleteButton = new Button("Delete", e -> {
                confirmDelete(actorId);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button backButton = Buttons.backButton(ActorView.class, "Back to actors");

            VerticalLayout buttonsLayout = new VerticalLayout();
            buttonsLayout.addClassName("buttons-layout");

            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.add(backButton);

            detailsLayout.setClassName("details-layout");
            detailsLayout.add(header, actorDetails, rating, actorMovies, buttonsLayout);
            mainLayout.add(detailsLayout, rightSideLayout);

            add(mainLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }

    private void confirmDelete(Long actorId) {
        Dialog dialog = new Dialog();
        var text = new Span("Do you want to remove Actor? This action cannot be undone.");

        Button confirmButton = new Button("Confirm", e -> {
            actorService.removeActor(actorId);
            dialog.close();
            UI.getCurrent().navigate(ActorView.class);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = Buttons.cancelButton(dialog);

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.getStyle().set("padding-top", "25px");

        dialog.add(text, buttons);
        dialog.open();
    }
}


package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
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
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.PersonDetails;
import pl.patrykkukula.MovieReviewPortal.View.Common.RatingStarsLayout;
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
            List<MovieDtoBasic> movies = actor.getMovies();

            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.addClassName("details-layout");

            VerticalLayout personDetails = new PersonDetails(
                    actor.getFirstName() + " " + actor.getLastName(),
                    actor.getCountry(),
                    actor.getDateOfBirth().toString(),
                    actor.getBiography(),
                    movies
            );

            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = actorService.fetchRateByActorIdAndUserId(actorId, userId);
            RatingStarsLayout ratingStarsLayout = new RatingStarsLayout(
                    actor.getRating(),
                    actor.getRateNumber(),
                    userDetailsService,
                    actorId,
                    rateDto,
                    (newRate, entityId) -> actorService.addRateToActor(new RateDto(newRate, entityId)),
                    entityId -> actorService.removeRate(entityId)
            );

            Button editButton = Buttons.editButton(ActorEditView.class, "Edit actor", actorId);
            Button deleteButton = new Button("Delete actor", e -> confirmDelete(actorId));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            Button backButton = Buttons.backButton(ActorView.class, "Back to actors");

            VerticalLayout buttonsLayout = new VerticalLayout();
            buttonsLayout.addClassName("buttons-layout");

            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.add(backButton);

            detailsLayout.add(personDetails, ratingStarsLayout, buttonsLayout);
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

        Button confirmButton = new Button("Confirm delete", e -> {
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


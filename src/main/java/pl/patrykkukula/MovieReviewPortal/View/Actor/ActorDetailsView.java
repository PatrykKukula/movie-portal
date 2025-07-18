package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.MoviePersonEntityLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PersonDetails;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.RatingStarsLayout;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

import java.io.IOException;

import static pl.patrykkukula.MovieReviewPortal.View.Common.PosterConstants.ACT_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.PosterConstants.ACT_DIR_PH;

@Route("actors")
@PageTitle("Actor")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class ActorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ActorServiceImpl actorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ImageServiceImpl imageService;

    public ActorDetailsView(ActorServiceImpl actorService, UserDetailsServiceImpl userDetailsService, ImageServiceImpl imageService) {
        this.actorService = actorService;
        this.userDetailsService = userDetailsService;
        this.imageService = imageService;
    }
    @Override
    public void setParameter(BeforeEvent event, Long actorId) {
        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);
        try {
            ActorDtoWithMovies actor = actorService.fetchActorByIdWithMovies(actorId);

            VerticalLayout personDetails = new PersonDetails(
                    actor.getCountry(),
                    actor.getDateOfBirth().toString()
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
            Button deleteButton = new Button("Delete actor", e -> CommonComponents.confirmDelete(
                    actorId, "Actor", y -> actorService.removeActor(y), ActorView.class));
            Button backButton = Buttons.backButton(ActorView.class, "Back to actors");

            VerticalLayout actorDataLayout = new VerticalLayout();
            actorDataLayout.addClassName("details-layout");

            MoviePersonEntityLayout moviePersonEntityLayout = new MoviePersonEntityLayout(
                    userDetailsService, imageService, editButton, deleteButton, backButton,
                    personDetails, ratingStarsLayout, actorId, ACT_DIR, ACT_DIR_PH,
                    actor.getFirstName(), actor.getLastName(), actor.getBiography(), actor.getMovies()
            );

            add(moviePersonEntityLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException | IOException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
}


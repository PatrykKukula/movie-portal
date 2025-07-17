package pl.patrykkukula.MovieReviewPortal.View.Actor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
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
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.UploadComponent;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.BiographyDiv;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviesDiv;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PersonDetails;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

import java.io.IOException;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.View.Common.PosterConstants.*;

@Route("actors")
@PageTitle("Actor")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class ActorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ActorServiceImpl actorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final IImageService imageService;

    public ActorDetailsView(ActorServiceImpl actorService, UserDetailsServiceImpl userDetailsService, IImageService imageService) {
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
            H3 header = new H3(actor.getFirstName() + " " + actor.getLastName());
            header.addClassName("header");

            VerticalLayout personDetails = new PersonDetails(
                    actor.getCountry(),
                    actor.getDateOfBirth().toString()
            );
            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = actorService.fetchRateByActorIdAndUserId(actorId, userId);
            Buttons.RatingStarsLayout ratingStarsLayout = new Buttons.RatingStarsLayout(
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

            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            Button backButton = Buttons.backButton(ActorView.class, "Back to actors");

            HorizontalLayout buttonsLayout = new HorizontalLayout();
            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.addClassName("buttons-layout");
            buttonsLayout.add(backButton);

            VerticalLayout detailsLayout = new VerticalLayout(personDetails);
            detailsLayout.getStyle().set("padding-left", "20px");
            detailsLayout.add(ratingStarsLayout);

            VerticalLayout actorDataLayout = new VerticalLayout();
            actorDataLayout.addClassName("details-layout");

            Upload upload = new UploadComponent(
              MAX_SIZE, MAX_SIZE_BYTES, ALLOWED_FORMAT, ALLOWED_TYPES, actorId, ACT_DIR, imageService
            );

            Poster poster = new Poster(imageService, actorId, ACT_DIR, ACT_DIR_PH   );
            HorizontalLayout posterDetailsLayout = new HorizontalLayout(poster, detailsLayout);

            Div biographyDiv = new BiographyDiv(actor.getBiography()) ;
            List<MovieDtoBasic> movies = actor.getMovies();
            Div moviesDiv = new MoviesDiv(movies);

            actorDataLayout.add(header, posterDetailsLayout);
            if (userDetailsService.isAdmin()) {
               actorDataLayout.add(upload);
            }
            buttonsLayout.addClassName("buttons-layout");
            buttonsLayout.add(backButton);
            actorDataLayout.add(biographyDiv, moviesDiv, buttonsLayout);

            HorizontalLayout commentsLayout = new HorizontalLayout();
            H3 secondSecHeader = new H3("Comments");
            commentsLayout.add(secondSecHeader);

            add(actorDataLayout, commentsLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException | IOException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
}


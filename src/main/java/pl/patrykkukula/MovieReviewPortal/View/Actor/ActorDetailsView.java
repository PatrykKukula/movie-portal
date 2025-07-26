package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.TopicSectionLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePersonEntityLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PersonDetails;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.RatingStarsLayout;

import java.io.IOException;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.INITIAL_PAGE;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.PAGE_SIZE;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.ACT_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.ACT_DIR_PH;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.RouteParametersConstants.TYPE_ACTOR;

@Slf4j
@Route("actors")
@PageTitle("Actor")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class ActorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ActorServiceImpl actorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ImageServiceImpl imageService;
    private final TopicServiceImpl topicService;

    public ActorDetailsView(ActorServiceImpl actorService, UserDetailsServiceImpl userDetailsService, ImageServiceImpl imageService, TopicServiceImpl topicService, TopicRepository topicRepository) {
        this.actorService = actorService;
        this.userDetailsService = userDetailsService;
        this.imageService = imageService;
        this.topicService = topicService;
    }
    @Override
    public void setParameter(BeforeEvent event, Long actorId) {
        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);
        try {
            ActorDtoWithMovies actor = actorService.fetchActorByIdWithMovies(actorId);

            PersonDetails personDetails = new PersonDetails(
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

            MoviePersonEntityLayout moviePersonEntityLayout = new MoviePersonEntityLayout(
                    userDetailsService, imageService, editButton, deleteButton, backButton,
                    personDetails, ratingStarsLayout, actorId, ACT_DIR, ACT_DIR_PH,
                    actor.getFirstName(), actor.getLastName(), actor.getBiography(), actor.getMovies()
            );
            TopicSectionLayout topicSectionLayout = new TopicSectionLayout(
                    topicService, userDetailsService, actorId, INITIAL_PAGE, PAGE_SIZE, "ASC", TYPE_ACTOR
            );

            add(moviePersonEntityLayout, topicSectionLayout);
        }
        catch (ResourceNotFoundException ex) {
            event.rerouteToError(ResourceNotFoundException.class, ex.getMessage());
        }
        catch (InvalidIdException ex){
            event.rerouteToError(InvalidIdException.class, ex.getMessage());
        }
        catch (IOException ex) {
            event.rerouteToError(IOException.class, ex.getMessage());
        }
    }
}


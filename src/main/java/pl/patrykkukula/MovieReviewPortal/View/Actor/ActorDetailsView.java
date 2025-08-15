package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson.PersonDetailsLayout;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.ACT_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.ACT_DIR_PH;

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
        PersonDetailsLayout<ActorDtoWithMovies> layout = new PersonDetailsLayout<>(
                actorService::fetchActorByIdWithMovies,
                actorId,
                event,
                userDetailsService,
                actorService::fetchRateByActorIdAndUserId,
                actorService::addRateToActor,
                actorService::removeRate,
                "Actor",
                actorService::removeActor,
                ActorView.class,
                imageService,
                ACT_DIR,
                ACT_DIR_PH,
                topicService
        );
        add(layout);
    }
}


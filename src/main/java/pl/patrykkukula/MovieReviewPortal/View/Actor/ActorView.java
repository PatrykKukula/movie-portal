package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorViewDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson.PersonLayout;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.ACT_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.ACT_DIR_PH;

@Route("actors")
@PageTitle("Actors")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class ActorView extends PersonLayout<ActorViewDto> {
    private final ActorServiceImpl actorService;
    private final IImageService imageService;
    private final UserDetailsServiceImpl userDetailsService;


    public ActorView(ActorServiceImpl actorService, IImageService imageService, UserDetailsServiceImpl userDetailsService) {
        super(
                "Actors",
                "Add actor",
                AddActorView.class,
                imageService,
                ACT_DIR,
                ACT_DIR_PH,
                actorService::fetchAllActorsView,
                actor -> createLayout(imageService, actor, ActorDetailsView.class, ACT_DIR, ACT_DIR_PH),
                userDetailsService
        );
        this.actorService = actorService;
        this.imageService = imageService;
        this.userDetailsService = userDetailsService;
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.SingleEntityLayoutWithPoster;

@Route("user/actors")
@PageTitle("Rated actors")
@AnonymousAllowed
public class RatedActorsView extends VerticalLayout implements HasUrlParameter<Long> {
    private final UserServiceImpl userService;
    private final ImageServiceImpl imageService;
    private final UserDetailsServiceImpl userDetailsService;

    public RatedActorsView(UserServiceImpl userService, ImageServiceImpl imageService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.imageService = imageService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        SingleEntityLayoutWithPoster<ActorDtoWithUserRate> singleEntityLayout = new SingleEntityLayoutWithPoster<>(
                userService::fetchAllRatedActors,
                userId,
                imageService
        );
        Div header = singleEntityLayout.setHeader(userId, userDetailsService);

        add(header, singleEntityLayout);
    }
}

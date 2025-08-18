package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.SingleEntityLayoutWithPoster;

@Route("user/movies")
@PageTitle("Rated movies")
@AnonymousAllowed
public class RatedMoviesView extends VerticalLayout implements HasUrlParameter<Long> {
    private final UserServiceImpl userService;
    private final ImageServiceImpl imageService;

    public RatedMoviesView(UserServiceImpl userService, ImageServiceImpl imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }
    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        SingleEntityLayoutWithPoster<MovieDtoWithUserRate> singleEntityLayout = new SingleEntityLayoutWithPoster<>(
                userService::fetchAllRatedMovies,
                userId,
                imageService
        );
        Div header = singleEntityLayout.setHeader(userId, userService);

        add(header, singleEntityLayout);
    }

}

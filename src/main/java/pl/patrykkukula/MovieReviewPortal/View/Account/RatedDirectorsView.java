package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.SingleEntityLayoutWithPoster;

@Route("user/directors")
@PageTitle("Rated directors")
@AnonymousAllowed
public class RatedDirectorsView extends VerticalLayout implements HasUrlParameter<Long> {
    private final UserServiceImpl userService;
    private final ImageServiceImpl imageService;

    public RatedDirectorsView(UserServiceImpl userService, ImageServiceImpl imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        SingleEntityLayoutWithPoster<DirectorDtoWithUserRate> singleEntityLayout = new SingleEntityLayoutWithPoster<>(
                userService::fetchAllRatedDirectors,
                userId,
                imageService
        );
        Div header = singleEntityLayout.setHeader(userId, userService);

        add(header, singleEntityLayout);
    }
}

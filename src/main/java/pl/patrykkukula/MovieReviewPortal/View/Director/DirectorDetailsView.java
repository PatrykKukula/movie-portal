package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson.PersonDetailsLayout;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.DIR_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.DIR_DIR_PH;

@Route("directors")
@PageTitle("Director details")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class DirectorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final DirectorServiceImpl directorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserServiceImpl userService;
    private final ImageServiceImpl imageService;
    private final TopicServiceImpl topicService;


    public DirectorDetailsView(DirectorServiceImpl directorService, UserDetailsServiceImpl userDetailsService, UserServiceImpl userService, ImageServiceImpl imageService, TopicServiceImpl topicService) {
        this.directorService = directorService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.imageService = imageService;
        this.topicService = topicService;
    }
    @Override
    public void setParameter(BeforeEvent event, Long directorId) {
        PersonDetailsLayout<DirectorDtoWithMovies> layout = new PersonDetailsLayout<>(
                directorService::fetchDirectorByIdWithMovies,
                directorId,
                event,
                userDetailsService,
                userService,
                directorService::fetchRateByDirectorIdAndUserId,
                directorService::addRateToDirector,
                directorService::removeRate,
                "Director",
                directorService::removeDirector,
                DirectorView.class,
                imageService,
                DIR_DIR,
                DIR_DIR_PH,
                topicService
        );
        add(layout);
    }
}

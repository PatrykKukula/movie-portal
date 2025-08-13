package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorViewDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson.PersonLayout;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.DIR_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.DIR_DIR_PH;


@Route("directors")
@PageTitle("Directors")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class DirectorView extends PersonLayout<DirectorViewDto> {
    private final DirectorServiceImpl directorService;
    private final IImageService imageService;
    private final UserDetailsServiceImpl userDetailsService;

    public DirectorView(DirectorServiceImpl directorService, IImageService imageService, UserDetailsServiceImpl userDetailsService) {
        super("Directors",
                "Add director",
                AddDirectorView.class,
                imageService,
                DIR_DIR,
                DIR_DIR_PH,
                directorService::fetchAllDirectorsView,
                director -> createLayout(imageService, director, DirectorDetailsView.class, DIR_DIR, DIR_DIR_PH),
                userDetailsService);
        this.directorService = directorService;
        this.imageService = imageService;
        this.userDetailsService = userDetailsService;
    }
}

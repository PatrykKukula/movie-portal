package pl.patrykkukula.MovieReviewPortal.View.Director;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.MoviePersonEntityLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PersonDetails;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.RatingStarsLayout;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;
import java.io.IOException;
import static pl.patrykkukula.MovieReviewPortal.View.Common.PosterConstants.DIR_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.PosterConstants.DIR_DIR_PH;

@Route("directors")
@PageTitle("Director details")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class DirectorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final DirectorServiceImpl directorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ImageServiceImpl imageService;

    public DirectorDetailsView(DirectorServiceImpl directorService, UserDetailsServiceImpl userDetailsService, ImageServiceImpl imageService) {
        this.directorService = directorService;
        this.userDetailsService = userDetailsService;
        this.imageService = imageService;
    }
    @Override
    public void setParameter(BeforeEvent event, Long directorId) {
        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);
        try {
            DirectorDtoWithMovies director = directorService.fetchDirectorByIdWithMovies(directorId);

            PersonDetails personDetails = new PersonDetails(
                    director.getCountry(),
                    director.getDateOfBirth().toString()
            );
            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = directorService.fetchRateByDirectorIdAndUserId(directorId, userId);
            RatingStarsLayout ratingStarsLayout = new RatingStarsLayout(
                    director.getRating(),
                    director.getRateNumber(),
                    userDetailsService,
                    directorId,
                    rateDto,
                    (newRate, entityId) -> directorService.addRateToDirector(new RateDto(newRate, entityId)),
                    entityId -> directorService.removeRate(entityId)
            );

            Button editButton = Buttons.editButton(DirectorEditView.class, "Edit director", directorId);
            Button deleteButton = new Button("Delete director", e -> CommonComponents.confirmDelete(
                    directorId, "Director", y -> directorService.removeDirector(y), DirectorView.class));
            Button backButton = Buttons.backButton(DirectorView.class, "Back to directors");

            MoviePersonEntityLayout moviePersonEntityLayout = new MoviePersonEntityLayout(
                    userDetailsService, imageService, editButton, deleteButton, backButton,
                    personDetails, ratingStarsLayout, directorId, DIR_DIR, DIR_DIR_PH,
                    director.getFirstName(), director.getLastName(), director.getBiography(), director.getMovies()
            );
            add(moviePersonEntityLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException | IOException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Director;

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
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
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

@Route("directors")
@PageTitle("Director details")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class DirectorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final DirectorServiceImpl directorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final IImageService imageService;

    public DirectorDetailsView(DirectorServiceImpl directorService, UserDetailsServiceImpl userDetailsService, IImageService imageService) {
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
            H3 header = new H3(director.getFirstName() + " " + director.getLastName());
            header.addClassName("header");

            PersonDetails personDetails = new PersonDetails(
                    director.getCountry(),
                    director.getDateOfBirth().toString()
            );
            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = directorService.fetchRateByDirectorIdAndUserId(directorId, userId);
            Buttons.RatingStarsLayout ratingStarsLayout = new Buttons.RatingStarsLayout(
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
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            Button backButton = Buttons.backButton(DirectorView.class, "Back to directors");
            HorizontalLayout buttonsLayout = new HorizontalLayout();

            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.addClassName("buttons-layout");
            buttonsLayout.add(backButton);

            VerticalLayout detailsLayout = new VerticalLayout(personDetails);
            detailsLayout.getStyle().set("padding-left", "20px");
            detailsLayout.add(ratingStarsLayout);

            VerticalLayout directorDataLayout = new VerticalLayout();
            directorDataLayout.addClassName("details-layout");

            Upload upload = new UploadComponent(
                    MAX_SIZE, MAX_SIZE_BYTES, ALLOWED_FORMAT, ALLOWED_TYPES, directorId, DIR_DIR, imageService
            );

            Poster poster = new Poster(imageService, directorId, DIR_DIR, DIR_DIR_PH   );
            HorizontalLayout posterDetailsLayout = new HorizontalLayout(poster, detailsLayout);

            Div biographyDiv = new BiographyDiv(director.getBiography());
            List<MovieDtoBasic> movies = director.getMovies();
            Div moviesDiv = new MoviesDiv(movies);

            directorDataLayout.add(header, posterDetailsLayout);
            if (userDetailsService.isAdmin()) {
                directorDataLayout.add(upload);
            }
            buttonsLayout.addClassName("buttons-layout");
            buttonsLayout.add(backButton);
            directorDataLayout.add(biographyDiv, moviesDiv, buttonsLayout);

            HorizontalLayout commentsLayout = new HorizontalLayout();
            H3 secondSecHeader = new H3("Comments");
            commentsLayout.add(secondSecHeader);

            add(directorDataLayout, commentsLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException | IOException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
}

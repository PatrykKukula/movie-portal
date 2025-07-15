package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Rate.RateDto;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Common.RatingStarsLayout;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

import java.io.IOException;

import static pl.patrykkukula.MovieReviewPortal.View.Account.AccountViewConstants.*;
import static pl.patrykkukula.MovieReviewPortal.View.Account.AccountViewConstants.AVATAR_SIZE_PX;

@Slf4j
@Route("movies")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class MovieDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final MovieServiceImpl movieService;
    private final UserDetailsServiceImpl userDetailsService;
    private final IImageService avatarService;

    public MovieDetailsView(MovieServiceImpl movieService, UserDetailsServiceImpl userDetailsService, IImageService avatarService) {
        this.movieService = movieService;
        this.userDetailsService = userDetailsService;
        this.avatarService = avatarService;
    }
    @Override
    public void setParameter(BeforeEvent event, Long movieId) {
        setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName("main-layout");

        VerticalLayout rightSideLayout = new VerticalLayout();
        try {
            MovieDtoWithDetails movie = movieService.fetchMovieDetailsById(movieId);
            H3 header = new H3(movie.getTitle());

            VerticalLayout detailsLayout = detailsLayout(movie);

            Long userId = userDetailsService.getAuthenticatedUserId();
            RateDto rateDto = movieService.fetchRateByMovieIdAndUserId(movieId, userId);
            RatingStarsLayout ratingStarsLayout = new RatingStarsLayout(
                    movie.getRating(),
                    movie.getRateNumber(),
                    userDetailsService,
                    movieId,
                    rateDto,
                    (newRate, entityId) -> movieService.addRateToMovie(new RateDto(newRate, entityId)),
                    entityId -> movieService.removeRate(entityId)
            );

            UI.getCurrent().getPage().setTitle("movie"); // DOES NOT WORK - FIGURE IT OUT

            Button editButton = new Button("Edit movie", e -> UI.getCurrent().navigate(MovieEditView.class, movieId));
            Button deleteButton = new Button("Delete movie", e -> confirmDelete(movieId));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button backButton = new Button("Back to movies", e -> UI.getCurrent().navigate(MovieView.class));

            VerticalLayout buttonsLayout = new VerticalLayout();
            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.add(backButton);
            buttonsLayout.addClassName("buttons-layout");

            Span actorsLabel = new Span("Actors");
            actorsLabel.getStyle().set("font-weight", "bold");
            Div actors = getActors(movie);

            VerticalLayout layout = new VerticalLayout();
            Upload upload = getUpload(movie);
            layout.addClassName("details-layout");
            Poster poster = new Poster(avatarService, movieId, "MoviePoster");
            layout.add(header, poster, detailsLayout, ratingStarsLayout, actorsLabel, actors, upload, buttonsLayout);

            mainLayout.add(layout, rightSideLayout);

            add(mainLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException | IOException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
    private void confirmDelete(Long movieId){
        Dialog dialog = new Dialog();
        var text = new Span("Do you want to remove Movie? This action cannot be undone.");

        Button confirmButton = new Button("Confirm delete", e -> {
            movieService.deleteMovie(movieId);
            dialog.close();
            UI.getCurrent().navigate(MovieView.class);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = Buttons.cancelButton(dialog);

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.getStyle().set("padding-top", "25px");

        dialog.add(text, buttons);
        dialog.open();
    }
    private static Div setDirector(MovieDtoWithDetails movie) {
        Div directorDiv = new Div();
        Span directorLabel = CommonComponents.labelSpan("Director: ");
        directorDiv.add(directorLabel);
        if (movie.getDirector() != null && movie.getDirector().getFirstName() != null && movie.getDirector().getLastName() != null) {
            DirectorDto director = movie.getDirector();
            Anchor directorAnchor = new Anchor("directors/" + director.getId(), director.getFirstName() + " " + director.getLastName());
            directorDiv.add(directorAnchor);
        }
        return directorDiv;
    }
    private VerticalLayout detailsLayout(MovieDtoWithDetails movie){
        Div directorSpan = setDirector(movie);
        Span categoryLabel = CommonComponents.labelSpan("Category: ");
        Span categorySpan = new Span(categoryLabel);
        categorySpan.add(movie.getCategory());
        Span releaseDateLabel = CommonComponents.labelSpan("Release date: ");
        Span releaseDateSpan = new Span(releaseDateLabel);
        releaseDateSpan.add(movie.getReleaseDate().toString());
        Span descriptionLabel = CommonComponents.labelSpan("Description: ");
        Span descriptionDiv = new Span(descriptionLabel);
        descriptionDiv.add(movie.getDescription());

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(false);
        detailsLayout.add(directorSpan, categorySpan, releaseDateSpan, descriptionDiv);
        return detailsLayout;
    }
    private Div getActors(MovieDtoWithDetails movie) {
        Div actors = new Div();
        actors.getStyle().set("white-space", "pre-line");
        movie.getActors()
                .forEach( actor -> {
                    var actorLink = new Anchor("actors/" + actor.getId(),"• " + actor.getFirstName() + " " + actor.getLastName() + "\n");
                    actors.add(actorLink);
                }
                );
        return actors;
    }
    private Upload getUpload(MovieDtoWithDetails movie) {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        UploadI18N I18N = new UploadI18N();
        UploadI18N.Error error = new UploadI18N.Error();
        error.setFileIsTooBig("File cannot exceed 10MB");
        error.setIncorrectFileType("Allowed file types: " + ALLOWED_FORMAT);
        I18N.setError(error);

        upload.setI18n(I18N);
        upload.setAcceptedFileTypes(ALLOWED_TYPES);
        upload.setMaxFileSize(MAX_SIZE_BYTES);
        upload.addSucceededListener(e ->{
            String id = String.valueOf(movie.getId());
            try {
                avatarService.saveImage(id, MAX_SIZE_BYTES, e.getMIMEType().substring(6), "MoviePoster", AVATAR_SIZE_PX, buffer.getInputStream());
            } catch (IOException ex) {
                String errorMessage = ex.getMessage();
                Notification notification = Notification.show(errorMessage, 2500,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        upload.addFileRejectedListener(e ->{
            String errorMessage = e.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 2500,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        return upload;
    }
}

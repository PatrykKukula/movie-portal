package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
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
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.UploadComponent;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.TopicSectionLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Rating.RatingStarsLayout;

import java.io.IOException;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.INITIAL_PAGE;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.PAGE_SIZE;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.*;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.RouteParametersConstants.TYPE_MOVIE;

@Slf4j
    @Route("movies")
    @CssImport("./styles/common-styles.css")
    @AnonymousAllowed
    public class MovieDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

        private final MovieServiceImpl movieService;
        private final UserDetailsServiceImpl userDetailsService;
        private final UserServiceImpl userService;
        private final IImageService imageService;
        private final TopicServiceImpl topicService;
        private static final String WIDTH = "210x";
        private static final String HEIGHT = "300px";

        public MovieDetailsView(MovieServiceImpl movieService, UserDetailsServiceImpl userDetailsService, UserServiceImpl userService, IImageService imageService, TopicServiceImpl topicService) {
            this.movieService = movieService;
            this.userDetailsService = userDetailsService;
            this.userService = userService;
            this.imageService = imageService;
            this.topicService = topicService;
        }
        @Override
        public void setParameter(BeforeEvent event, Long movieId) {
            addClassName("main-layout");
            setAlignItems(Alignment.CENTER);
            try {
                MovieDtoWithDetails movie = movieService.fetchMovieDetailsById(movieId);
                H3 header = new H3(movie.getTitle());
                header.addClassName("header");

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

                Button editButton = new Button("Edit movie", e -> UI.getCurrent().navigate(MovieEditView.class, movieId));
                Button deleteButton = new Button("Delete movie", e -> CommonComponents.confirmDelete(
                        movieId, "Movie", y -> movieService.deleteMovie(y), MovieView.class));
                deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                Button backButton = new Button("Back to movies", e -> UI.getCurrent().navigate(MovieView.class));
                HorizontalLayout buttonsLayout = new HorizontalLayout();

                if (userDetailsService.isAdmin()) {
                    buttonsLayout.add(editButton, deleteButton);
                }
                buttonsLayout.addClassName("buttons-layout");
                buttonsLayout.add(backButton);

                VerticalLayout detailsLayout = detailsLayout(movie);
                detailsLayout.getStyle().set("padding-left", "20px");
                Div actors = getActors(movie);
                detailsLayout.add(ratingStarsLayout);

                VerticalLayout movieDataLayout = new VerticalLayout();
                movieDataLayout.addClassName("details-layout");

                Upload upload = new UploadComponent(
                  MAX_SIZE, MAX_SIZE_BYTES, ALLOWED_FORMAT, ALLOWED_TYPES, movieId, MOV_DIR, imageService
                );
                Poster poster = new Poster(imageService, movieId, MOV_DIR, MOV_DIR_PH, WIDTH, HEIGHT);

                HorizontalLayout posterDetailsLayout = new HorizontalLayout(poster, detailsLayout);

                Div descriptionDiv = descriptionDiv(movie);

                movieDataLayout.add(header, posterDetailsLayout);
                if (userDetailsService.isAdmin()) {
                    movieDataLayout.add(upload);
                }
                movieDataLayout.add(descriptionDiv, actors, buttonsLayout);

                TopicSectionLayout topicSectionLayout = new TopicSectionLayout(
                        topicService, userDetailsService, userService, movieId, INITIAL_PAGE, PAGE_SIZE, "ASC", TYPE_MOVIE
                );

                add(movieDataLayout, topicSectionLayout);
            }
            catch (ResourceNotFoundException ex) {
                event.rerouteToError(ResourceNotFoundException.class, ex.getMessage());
            }
            catch (InvalidIdException ex){
                event.rerouteToError(InvalidIdException.class, ex.getMessage());
            }
            catch (IOException ex) {
                event.rerouteToError(IOException.class, ex.getMessage());
            }
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

            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.setPadding(false);
            detailsLayout.add(directorSpan, categorySpan, releaseDateSpan);
            return detailsLayout;
        }
        private Div descriptionDiv (MovieDtoWithDetails movie){
            Span descriptionLabel = CommonComponents.labelSpan("Description: ");
            Div descriptionDiv = new Div(descriptionLabel);
            descriptionDiv.add(movie.getDescription());
            return descriptionDiv;
        }
        private Div getActors(MovieDtoWithDetails movie) {
            Div actors = new Div();
            Span actorsLabel = new Span("Actors\n");
            actorsLabel.getStyle().set("font-weight", "bold");
            actors.add(actorsLabel);
            actors.getStyle().set("white-space", "pre-line");
            movie.getActors()
                    .forEach( actor -> {
                        var actorLink = new Anchor("actors/" + actor.getId(),"• " + actor.getFirstName() + " " + actor.getLastName() + "\n");
                        actors.add(actorLink);
                    }
                    );
            return actors;
        }
    }

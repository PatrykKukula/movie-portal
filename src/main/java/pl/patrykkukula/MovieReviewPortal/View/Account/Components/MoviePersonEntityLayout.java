package pl.patrykkukula.MovieReviewPortal.View.Account.Components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.BiographyDiv;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviesDiv;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.RatingStarsLayout;

import java.io.IOException;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.View.Common.PosterConstants.*;

public class MoviePersonEntityLayout extends VerticalLayout {

    public MoviePersonEntityLayout(UserDetailsServiceImpl userDetailsService, ImageServiceImpl imageService,
                                   Button editButton, Button deleteButton, Button backButton,
                                   VerticalLayout personDetails, RatingStarsLayout ratingStarsLayout, Long entityId, String dir, String dirPh,
                                   String firstName, String lastName, String biography, List<MovieDtoBasic> movies) throws IOException {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        if (userDetailsService.isAdmin()) {
            buttonsLayout.add(editButton, deleteButton);
        }
        buttonsLayout.addClassName("buttons-layout");
        buttonsLayout.add(backButton);

        VerticalLayout detailsLayout = new VerticalLayout(personDetails);
        detailsLayout.getStyle().set("padding-left", "20px");
        detailsLayout.add(ratingStarsLayout);

        VerticalLayout actorDataLayout = new VerticalLayout();
        actorDataLayout.addClassName("details-layout");

        Upload upload = new UploadComponent(
                MAX_SIZE, MAX_SIZE_BYTES, ALLOWED_FORMAT, ALLOWED_TYPES, entityId, dir, imageService
        );

        Poster poster = new Poster(imageService, entityId, dir, dirPh);
        HorizontalLayout posterDetailsLayout = new HorizontalLayout(poster, detailsLayout);

        Div biographyDiv = new BiographyDiv(biography);
        Div moviesDiv = new MoviesDiv(movies);

        H3 header = new H3(firstName + " " + lastName);
        header.addClassName("header");

        actorDataLayout.add(header, posterDetailsLayout);
        if (userDetailsService.isAdmin()) {
            actorDataLayout.add(upload);
        }
        buttonsLayout.addClassName("buttons-layout");
        buttonsLayout.add(backButton);
        actorDataLayout.add(biographyDiv, moviesDiv, buttonsLayout);

        HorizontalLayout commentsLayout = new HorizontalLayout();
        H3 secondSecHeader = new H3("Comments");
        commentsLayout.add(secondSecHeader);

        setAlignItems(Alignment.CENTER);
        add(actorDataLayout, commentsLayout);
    }

}

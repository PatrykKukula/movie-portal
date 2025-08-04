package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

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
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.UploadComponent;

import java.io.IOException;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.*;

public class MoviePersonEntityLayout extends VerticalLayout {
    /*
        DIV to display movie person details
     */
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

        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.addClassName("details-layout");

        Upload upload = new UploadComponent(
                MAX_SIZE, MAX_SIZE_BYTES, ALLOWED_FORMAT, ALLOWED_TYPES, entityId, dir, imageService
        );

        Poster poster = new Poster(imageService, entityId, dir, dirPh);
        HorizontalLayout posterDetailsLayout = new HorizontalLayout(poster, detailsLayout);

        Div biographyDiv = new BiographyDiv(biography);
        Div moviesDiv = new MoviesDiv(movies);

        H3 header = new H3(firstName + " " + lastName);
        header.addClassName("header");

        dataLayout.add(header, posterDetailsLayout);
        if (userDetailsService.isAdmin()) {
            dataLayout.add(upload);
        }
        buttonsLayout.addClassName("buttons-layout");
        buttonsLayout.add(backButton);
        dataLayout.add(biographyDiv, moviesDiv, buttonsLayout);

        setAlignItems(Alignment.CENTER);
        add(dataLayout);
    }

}

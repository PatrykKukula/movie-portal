package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.PersonDetails;
import pl.patrykkukula.MovieReviewPortal.View.Common.RatingStarsLayout;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

import java.util.List;

@Route("directors")
@PageTitle("Director details")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class DirectorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final DirectorServiceImpl directorService;
    private final UserDetailsServiceImpl userDetailsService;

    public DirectorDetailsView(DirectorServiceImpl directorService, UserDetailsServiceImpl userDetailsService) {
        this.directorService = directorService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long directorId) {
        setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName("main-layout");
        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setWidth("55%");

        try {
            DirectorDtoWithMovies director = directorService.fetchDirectorByIdWithMovies(directorId);
            List<MovieDtoBasic> movies = director.getMovies();
            VerticalLayout detailsLayout = new VerticalLayout();
            detailsLayout.addClassName("details-layout");

            PersonDetails personDetails = new PersonDetails(
                    director.getFirstName() + " " + director.getLastName(),
                    director.getCountry(),
                    director.getDateOfBirth().toString(),
                    director.getBiography(),
                    movies
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
            Button deleteButton = new Button("Delete director", e -> confirmDelete(directorId));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            Button backButton = Buttons.backButton(DirectorView.class, "Back to directors");

            VerticalLayout buttonsLayout = new VerticalLayout();
            if (userDetailsService.isAdmin()) {
                buttonsLayout.add(editButton, deleteButton);
            }
            buttonsLayout.add(backButton);
            buttonsLayout.addClassName("buttons-layout");

            detailsLayout.add(personDetails, ratingStarsLayout, buttonsLayout);
            mainLayout.add(detailsLayout, rightSideLayout);

            add(mainLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }
    private void confirmDelete(Long directorId){
        Dialog dialog = new Dialog();
        Span text = new Span("Do you want to remove Director? This action cannot be undone.");

        Button confirmButton = new Button("Confirm delete", e -> {
            directorService.removeDirector(directorId);
            dialog.close();
            UI.getCurrent().navigate(DirectorView.class);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = Buttons.cancelButton(dialog);

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.getStyle().set("padding-top", "25px");

        dialog.add(text, buttons);
        dialog.open();
    }
}

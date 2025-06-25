package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithMovies;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Exception.InvalidIdException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Fallback.ResourceNotFoundFallback;

import java.util.List;

@Route("directors")
@PageTitle("Director details")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class DirectorDetailsView extends VerticalLayout implements HasUrlParameter<Long> {

    private final DirectorServiceImpl directorService;

    public DirectorDetailsView(DirectorServiceImpl directorService) {
        this.directorService = directorService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long directorId) {
        setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addClassName("main-layout");
        VerticalLayout rightSideLayout = new VerticalLayout();
        rightSideLayout.setWidth("70%");
        try {
            DirectorDtoWithMovies director = directorService.fetchDirectorByIdWithMovies(directorId);

            VerticalLayout detailsLayout = new VerticalLayout();
            H3 header = new H3(director.getFirstName() + " " + director.getLastName());
            List<MovieDtoBasic> movies = director.getMovies();

            Div country = new Div(director.getCountry());
            Div dateOfBirth = new Div(director.getDateOfBirth().toString());
            Div biography = new Div(director.getBiography());

            Div directorDetails = new Div();
            directorDetails.addClassName("details");
            directorDetails.setText("Country: " + country.getText() + "\nDate of birth: " + dateOfBirth.getText()
                    + "\nBiography: " + biography.getText());

            Div directorMovies = new Div();
            directorMovies.setClassName("movies");
            directorMovies.add(new H5("Movies"));

            for(MovieDtoBasic movie : movies) {
                Anchor movieLink = new Anchor("movies/" + movie.getId(), "• " + movie.getTitle());
                movieLink.addClassName("details");
                directorMovies.add(movieLink);
            }

            Button editButton = Buttons.editButton(DirectorEditView.class, directorId);
            Button deleteButton = new Button("Delete", e -> {
                confirmDelete(directorId);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button backButton = Buttons.backButton(DirectorView.class, "Back to directors");

            VerticalLayout buttonsLayout = new VerticalLayout();
            buttonsLayout.addClassName("buttons-layout");
            buttonsLayout.add(editButton,deleteButton,backButton);

            detailsLayout.add(header, directorDetails, directorMovies, buttonsLayout);
            detailsLayout.setClassName("details-layout");

            mainLayout.add(detailsLayout, rightSideLayout);

            add(mainLayout);
        }
        catch (ResourceNotFoundException | InvalidIdException ex) {
            event.forwardTo(ResourceNotFoundFallback.class, ex.getMessage());
        }
    }

    private void confirmDelete(Long directorId){
        Dialog dialog = new Dialog();
        var text = new Span("Do you want to remove Director? This action cannot be undone.");

        Button confirmButton = new Button("Confirm", e -> {
            directorService.removeDirector(directorId);
            dialog.close();
            UI.getCurrent().navigate(DirectorView.class);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = Buttons.cancelButton(dialog);
        cancelButton.addThemeVariants();

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.getStyle().set("padding-top", "25px");

        dialog.add(text, buttons);
        dialog.open();
    }
}

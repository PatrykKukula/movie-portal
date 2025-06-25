package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Actor.AddActorView;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("directors")
@PageTitle("Directors")
@AnonymousAllowed
public class DirectorView extends VerticalLayout {
    private final DirectorServiceImpl directorService;
    private final Grid<DirectorDto> grid = new Grid<>(DirectorDto.class);
    private final UserDetailsServiceImpl userDetailsService;

    public DirectorView(DirectorServiceImpl directorService, UserDetailsServiceImpl userDetailsService) {
        this.directorService = directorService;
        this.userDetailsService = userDetailsService;
        configureGrid();

        H1 title = new H1("Directors");
        title.getStyle().set("font-size","36px").set("font-family", "cursive");

        Button addDirector = Buttons.addButton(AddDirectorView.class, "Add director");

        TextField searchField = FormFields.searchField("Search by name or last name","Enter name or last name...");
        searchField.addValueChangeListener(event -> updateGridData(event.getValue()));

        add(title, searchField, grid);
        if (userDetailsService.getAuthenticatedUser() != null) {
            addComponentAtIndex(1, addDirector);
        }
        updateGridData("");
    }

    private void configureGrid() {
        grid.setColumns("firstName", "lastName", "country", "dateOfBirth");
        grid.asSingleSelect().addValueChangeListener(event -> {
            DirectorDto selectedDirector = event.getValue();
            if (selectedDirector != null) {
                UI.getCurrent().navigate(DirectorDetailsView.class, selectedDirector.getId());
            }
        });
    }

    private void updateGridData(String search) {
        List<DirectorDto> directors;
        if (search == null) {
            directors = directorService.fetchAllDirectors("ASC");
        }
        else {
            directors = directorService.fetchAllDirectorsByNameOrLastName(search, "ASC");
        }
        grid.setItems(directors);
    }
}

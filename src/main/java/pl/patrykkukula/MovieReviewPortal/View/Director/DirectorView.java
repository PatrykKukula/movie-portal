package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorViewDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;


@Route("directors")
@PageTitle("Directors")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class DirectorView extends VerticalLayout {
    private final DirectorServiceImpl directorService;
    private final Grid<DirectorViewDto> grid = new Grid<>(DirectorViewDto.class);
    private final UserDetailsServiceImpl userDetailsService;

    public DirectorView(DirectorServiceImpl directorService, UserDetailsServiceImpl userDetailsService) {
        this.directorService = directorService;
        this.userDetailsService = userDetailsService;
        configureGrid();

        H1 title = new H1("Directors");
        title.addClassName("view-title");

        Button addDirector = Buttons.addButton(AddDirectorView.class, "Add director");

        TextField searchField = FormFields.searchField("Search by name or last name","Enter name or last name...");
        searchField.addValueChangeListener(event -> updateGridData(event.getValue()));

        add(title, searchField, grid);
        if (userDetailsService.isAdmin()) {
            addComponentAtIndex(1, addDirector);
        }
        updateGridData("");
    }
    private void configureGrid() {
        grid.setColumns("firstName", "lastName", "country", "dateOfBirth");
        grid.addColumn(dto -> String.format("%.2f",dto.getRate())).setHeader("Rate").setWidth("20%");
        grid.asSingleSelect().addValueChangeListener(event -> {
            DirectorViewDto selectedDirector = event.getValue();
            if (selectedDirector != null) {
                UI.getCurrent().navigate(DirectorDetailsView.class, selectedDirector.getId());
            }
        });
    }
    private void updateGridData(String searchText) {
        List<DirectorViewDto> directors;
        directors = directorService.fetchAllDirectorsView(searchText);
        grid.setItems(directors);
    }
}

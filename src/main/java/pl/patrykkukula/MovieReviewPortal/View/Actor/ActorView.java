package pl.patrykkukula.MovieReviewPortal.View.Actor;

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
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorViewDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("actors")
@PageTitle("Actors")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class ActorView extends VerticalLayout {
    private final ActorServiceImpl actorService;
    private final Grid<ActorViewDto> grid = new Grid<>(ActorViewDto.class);
    private final UserDetailsServiceImpl userDetailsService;

    public ActorView(ActorServiceImpl actorService, UserDetailsServiceImpl userDetailsService) {
        this.actorService = actorService;
        this.userDetailsService = userDetailsService;
        configureGrid();

        H1 title = new H1("Actors");
        title.addClassName("view-title");

        Button addActor = Buttons.addButton(AddActorView.class, "Add actor");
        TextField searchField = FormFields.searchField("Search by name or last name","Enter name or last name...");
        searchField.addValueChangeListener(e -> updateGridData(e.getValue()));

        add(title, searchField, grid);
        if (userDetailsService.isAdmin()) {
            addComponentAtIndex(1, addActor);
        }
        updateGridData("");
    }
    private void configureGrid() {
        grid.setColumns("firstName", "lastName", "country", "dateOfBirth");
        grid.addColumn(dto -> String.format("%.2f",dto.getRate())).setHeader("Rate").setWidth("20%");
        grid.asSingleSelect().addValueChangeListener(event -> {
            ActorViewDto selectedActor = event.getValue();
            if (selectedActor != null) {
                UI.getCurrent().navigate(ActorDetailsView.class, selectedActor.getId());
            }
        });
    }
    private void updateGridData(String searchText) {
        List<ActorViewDto> actors;
        actors = actorService.fetchAllActorsView(searchText);
        grid.setItems(actors);
    }
}

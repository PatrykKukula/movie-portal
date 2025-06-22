package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("actors")
@PageTitle("Actors")
public class ActorView extends VerticalLayout {
    private final ActorServiceImpl actorService;
    private final Grid<ActorDto> grid = new Grid<>(ActorDto.class);

    public ActorView(ActorServiceImpl actorService) {
        this.actorService = actorService;

        H1 title = new H1("Actors");
        title.getStyle().set("font-size","36px").set("font-family", "cursive");

        configureGrid();

        Button addActor = new Button("Add actor");
        addActor.setPrefixComponent(VaadinIcon.PLUS.create());
        addActor.addClickListener(e -> UI.getCurrent().navigate(AddActorView.class));

        TextField searchField = FormFields.searchField("Search by name or last name","Enter name or last name...");
        searchField.addValueChangeListener(e -> updateGridData(e.getValue()));

        add(title, addActor, searchField, grid);
        updateGridData("");
    }

    private void configureGrid() {
        grid.setColumns("firstName", "lastName", "country", "dateOfBirth");
        grid.asSingleSelect().addValueChangeListener(event -> {
            ActorDto selectedActor = event.getValue();
            if (selectedActor != null) {
                UI.getCurrent().navigate(ActorDetailsView.class, selectedActor.getId());
            }
        });
    }

    private void updateGridData(String searchText) {
        List<ActorDto> actors;
        if (searchText == null) {
            actors = actorService.fetchAllActors("ASC");
        }
        else {
            actors = actorService.fetchAllActorsByNameOrLastName(searchText, "ASC");
        }
        grid.setItems(actors);
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("movies")
@PageTitle("Movies")
public class MovieView extends VerticalLayout {

    private final MovieServiceImpl movieService;
    private final Grid<MovieDtoBasic> grid = new Grid<>(MovieDtoBasic.class);

    public MovieView(MovieServiceImpl movieService) {
        this.movieService = movieService;

        H1 title = new H1("Movies");
        title.getStyle().set("font-size","36px").set("font-family", "cursive");

        configureGrid();

        Button addMovie = new Button("Add Movie");
        addMovie.setPrefixComponent(VaadinIcon.PLUS.create());
        addMovie.addClickListener(e -> UI.getCurrent().navigate(AddMovieView.class));

        TextField searchField = FormFields.searchField("Search by title", "Enter title...");
        searchField.addValueChangeListener(event -> updateGridData(event.getValue()));

        add(title, addMovie, searchField, grid);
        updateGridData("");
    }

    private void configureGrid() {
        grid.setColumns("title", "category");
        grid.setWidth("50%");
        grid.getColumnByKey("title").setWidth("60%");
        grid.getColumnByKey("category").setWidth("40%");

        grid.asSingleSelect().addValueChangeListener(event -> {
            MovieDtoBasic selectedMovie = event.getValue();
            if (selectedMovie != null) {
                UI.getCurrent().navigate(MovieDetailsView.class, selectedMovie.getId());
            }
        });
    }

    private void updateGridData(String searchedText) {
        List<MovieDtoBasic> movies;
        if (searchedText == null) {
            movies = movieService.fetchAllMovies("ASC");
        } else {
            movies = movieService.fetchAllMoviesByTitle(searchedText, "ASC");
        }
        grid.setItems(movies);
    }
}



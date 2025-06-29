package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieViewDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("movies")
@PageTitle("Movies")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class MovieView extends VerticalLayout {

    private final MovieServiceImpl movieService;
    private final Grid<MovieViewDto> grid = new Grid<>(MovieViewDto.class);
    private final UserDetailsServiceImpl userDetailsService;
    private final ComboBox<MovieCategory> categoryComboBox = FormFields.categoryComboBox(false);

    public MovieView(MovieServiceImpl movieService, UserDetailsServiceImpl userDetailsService) {
        this.movieService = movieService;
        this.userDetailsService = userDetailsService;
        configureGrid();

        H1 title = new H1("Movies");
        title.addClassName("view-title");

        Button addMovie = Buttons.addButton(AddMovieView.class, "Add movie");
        TextField searchField = FormFields.searchField("Search by title", "Enter title...");
        searchField.addValueChangeListener(event -> updateGridData(event.getValue()));
        categoryComboBox.addValueChangeListener(e -> updateGridData(searchField.getValue()));

        HorizontalLayout searchFieldLayout = new HorizontalLayout(searchField, categoryComboBox);
        searchFieldLayout.setSizeFull();
        add(title, searchFieldLayout, grid);

        if (userDetailsService.isAdmin()) {
            addComponentAtIndex(1, addMovie);
        }
        updateGridData("");
    }

    private void configureGrid() {
        grid.setColumns("title", "category", "rating", "releaseDate");
        grid.getColumnByKey("title").setWidth("30%");
        grid.getColumnByKey("category").setWidth("30%");
        grid.getColumnByKey("rating").setWidth("20%");
        grid.getColumnByKey("releaseDate").setWidth("20%");

        grid.asSingleSelect().addValueChangeListener(event -> {
            MovieViewDto selectedMovie = event.getValue();
            if (selectedMovie != null) {
                UI.getCurrent().navigate(MovieDetailsView.class, selectedMovie.getId());
            }
        });
    }
    private void updateGridData(String searchedTitle) {
        List<MovieViewDto> movies;
        if (categoryComboBox.getValue() == MovieCategory.NONE) {
            movies = movieService.fetchAllMoviesForView(searchedTitle);
        }
        else {
            MovieCategory category = categoryComboBox.getValue();
            movies = movieService.fetchAllMoviesForViewByCategory(category, searchedTitle);
        }
        grid.setItems(movies);
    }
}



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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Actor.AddActorView;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("movies")
@PageTitle("Movies")
@AnonymousAllowed
public class MovieView extends VerticalLayout {

    private final MovieServiceImpl movieService;
    private final Grid<MovieDtoBasic> grid = new Grid<>(MovieDtoBasic.class);
    private final UserDetailsServiceImpl userDetailsService;

    public MovieView(MovieServiceImpl movieService, UserDetailsServiceImpl userDetailsService) {
        this.movieService = movieService;
        this.userDetailsService = userDetailsService;
        configureGrid();

        H1 title = new H1("Movies");
        title.getStyle().set("font-size","36px").set("font-family", "cursive");

        Button addMovie = Buttons.addButton(AddMovieView.class, "Add movie");
        TextField searchField = FormFields.searchField("Search by title", "Enter title...");
        searchField.addValueChangeListener(event -> updateGridData(event.getValue()));

        add(title, searchField, grid);
        if (userDetailsService.getAuthenticatedUser() != null) {
            addComponentAtIndex(1, addMovie);
        }
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



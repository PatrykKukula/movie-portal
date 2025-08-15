package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieViewDto;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PagedList;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PageButtons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.MOV_DIR;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.MOV_DIR_PH;

@Slf4j
@Route("movies")
@PageTitle("Movies")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class MovieView extends VerticalLayout {

    private final MovieServiceImpl movieService;
    private final IImageService imageService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ComboBox<MovieCategory> categoryComboBox = FormFields.categoryComboBox(false, true);
    private List<MovieViewDto> movies;
    private PagedList<MovieViewDto> pagedList;
    private PageButtons pageButtons;
    private int currentPage = 0;
    private int totalPages;
    private final int PAGE_SIZE = 2;
    private final VerticalLayout moviesLayout;
    private static final String WIDTH = "100px";
    private static final String HEIGHT = "140px";

    public MovieView(MovieServiceImpl movieService, IImageService imageService, UserDetailsServiceImpl userDetailsService) {
        this.movieService = movieService;
        this.imageService = imageService;
        this.userDetailsService = userDetailsService;
        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("Movies");
        title.addClassName("view-title");

        Button addMovie = Buttons.addButton(AddMovieView.class, "Add movie");

        TextField searchField = FormFields.searchField("Search by title", "Enter title...");

        movies = movieService.fetchAllMoviesForView(null,"ASC", null);
        pagedList = new PagedList<>(movies, PAGE_SIZE);
        totalPages = pagedList.getTotalPages();

        moviesLayout = new VerticalLayout();
        moviesLayout.addClassName("details-layout");

        pageButtons = new PageButtons(currentPage, totalPages);

        Consumer<VerticalLayout> consumer = renderPage();
        consumer.accept(moviesLayout);

        pageButtons.setUp(moviesLayout, pagedList, consumer);
        ComboBox<String> sortingBox = sortingComboBox();
        Button filterButton = filterButton(searchField, moviesLayout, sortingBox);
        HorizontalLayout searchFieldLayout = new HorizontalLayout(searchField, categoryComboBox, sortingBox, filterButton);
        searchFieldLayout.addClassName("details-layout");
        searchFieldLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        searchFieldLayout.setAlignItems(Alignment.CENTER);
        searchFieldLayout.setHeight("100px");

        add(title, searchFieldLayout, moviesLayout, pageButtons);

        if (userDetailsService.isAdmin()) {
            addComponentAtIndex(1, addMovie);
        }
    }
    private Consumer<VerticalLayout> renderPage(){
        Consumer<VerticalLayout> consumer = verticalLayout -> {
            currentPage = pageButtons.getCurrentPage();
            List<MovieViewDto> page = pagedList.getPage(currentPage);
            verticalLayout.removeAll();
            for (MovieViewDto movie : page) {
                verticalLayout.add(singleMovieLayout(movie));
            }
        };
        return consumer;
    }
    private HorizontalLayout singleMovieLayout(MovieViewDto movie){
        HorizontalLayout layout = new HorizontalLayout();

        Div rightSide = new Div();
        Poster poster;

        try {
            poster = new Poster(imageService, movie.getId(), MOV_DIR, MOV_DIR_PH, WIDTH, HEIGHT);
            rightSide.add(poster);
        }
        catch (IOException ex){
            log.warn("Error loading user avatar:{} ", ex.getMessage());
        }

        VerticalLayout movieDetails = movieDetails(movie);
        movieDetails.setWidthFull();

        layout.add(rightSide, movieDetails);
        layout.getStyle().set("border-bottom", "1px solid lightgrey").set("cursor", "pointer");
        layout.setWidthFull();

        layout.addClickListener(e -> UI.getCurrent().navigate(MovieDetailsView.class, movie.getId()));

        return layout;
    }
    private VerticalLayout movieDetails(MovieViewDto movie){
        VerticalLayout movieDetails = new VerticalLayout();
        Div title = new Div(new Span(CommonComponents.labelSpan("Title: ")), new Span(movie.getTitle()));
        Div category = new Div(new Span(CommonComponents.labelSpan("Category: ")), new Span(movie.getCategory().toString()));
        Div releaseDate = new Div(new Span(CommonComponents.labelSpan("Release date: ")), new Span(movie.getReleaseDate().toString()));
        movieDetails.add(title, category, releaseDate);
        Div rating = ratingDiv(movie);
        movieDetails.add(rating);
        movieDetails.getStyle().set("padding", "0 0 35px 15px");
        return movieDetails;
    }
    private Div ratingDiv(MovieViewDto movie){
        Double averageRate = movie.getAverageRate();
        Integer rateNumber = movie.getRateNumber();

        Div rating = new Div(new Span(CommonComponents.labelSpan("Rate: ")), new Span(" %s with %s votes".formatted(String.format("%.2f", averageRate), rateNumber)));

        Div ratingDiv = new Div();
        ratingDiv.add(rating);
        return ratingDiv;
    }
    private Button filterButton(TextField searchField, VerticalLayout layout, ComboBox<String> sortingBox){
        Button button = new Button("Search");
        button.getStyle().set("margin-top", "37px");
        button.addClickListener(e -> {
            String title = searchField.getValue();
            MovieCategory category = categoryComboBox.getValue();
            String sort = sortingBox.getValue();

            List<MovieViewDto> movies = movieService.fetchAllMoviesForView(title, sort != null ? sort : "ASC", category);
            pagedList.setList(movies);
            Consumer<VerticalLayout> consumer = renderPage();
            pageButtons.setUp(layout, pagedList, consumer);
            consumer.accept(layout);
        });
        return button;
    }
    private ComboBox<String> sortingComboBox(){
        ComboBox<String> comboBox = new ComboBox<>("Order by title");
        comboBox.setItems(List.of("ASC", "DESC"));
        comboBox.setItemLabelGenerator(e -> {
            if (e.equals("ASC")) return "Ascending";
            else return "Descending";
        });
        comboBox.setValue("ASC");
        return comboBox;
    }
}



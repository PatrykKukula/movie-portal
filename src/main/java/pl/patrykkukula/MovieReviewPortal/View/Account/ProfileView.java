package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDtoWithUserRate;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Constants.AccountViewConstants;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image.AvatarImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.SingleEntityLayoutWithPoster;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.reflections.Reflections.log;

@Route("user")
@PageTitle("Profile details")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class ProfileView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ImageServiceImpl imageService;
    private final UserServiceImpl userService;
    private final SingleEntityLayoutWithPoster<?> singleEntityLayout;
    private final Tab actorTab = new Tab("Actors");
    private final Tab directorTab = new Tab("Directors");
    private final Tab movieTab = new Tab("Movies");
    private final Tabs tabs;
    private final Button seeAllButton = new Button("See all");
    private final String AVATAR_WIDTH = "180px";
    private final String AVATAR_HEIGHT = "180px";
    private final String WIDTH = "140px";
    private final String HEIGHT = "180px";

    public ProfileView(ImageServiceImpl imageService, UserServiceImpl userService) {
        this.imageService = imageService;
        this.userService = userService;
        singleEntityLayout = new SingleEntityLayoutWithPoster<>(imageService);
        addClassName("main-layout");
        seeAllButton.getStyle().set("align-self", "center");
        tabs = new Tabs(movieTab, actorTab, directorTab);
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        UserEntity userEntity = userService.loadUserEntityByIdVaadin(userId);
        HorizontalLayout userDetailsLayout = new HorizontalLayout();
        userDetailsLayout.addClassName("details-layout");
        userDetailsLayout.getStyle().set("padding-bottom", "20px");

        Button browseCommentsButton = browseCommentsButton(userEntity.getUsername());

        if (userEntity == null) add(userNotFound());
        else {
            try {
                AvatarImpl avatar = new AvatarImpl(imageService, AVATAR_WIDTH, AVATAR_HEIGHT, AccountViewConstants.DIR, AccountViewConstants.DIR_PH, userId);
                userDetailsLayout.add(avatar);
            } catch (IOException ex) {
                log.warn("Error loading user avatar:{} ", ex.getMessage(), ex);
            }
            VerticalLayout personalData = personalDataLayout(userEntity);

            userDetailsLayout.add(personalData);

            Div statsHeader = new Div("Statistics");
            statsHeader.addClassName("detail");
            statsHeader.getStyle().set("align-self", "center");

            VerticalLayout statistics = statisticsLayout(userId, userEntity);
            VerticalLayout top5Rated = topRatedLayout(userId);
            top5Rated.add(seeAllButton);

            add(userDetailsLayout, browseCommentsButton, statsHeader, statistics, top5Rated);
        }
    }
    private VerticalLayout statisticsLayout(Long userId, UserEntity user){
        VerticalLayout statistics = new VerticalLayout();
        VerticalLayout avgRate = avgMovieRate(userId);
        VerticalLayout mostRated = mostRatedCategoryLayout(userId);
        VerticalLayout movieRateCount = movieRateCountLayout(userId);
        VerticalLayout actorRateCount = actorRateCountLayout(userId);
        VerticalLayout directorRateCount = directorRateCountLayout(userId);
        VerticalLayout commentCount = commentCountLayout(user);

        avgRate.setWidth("300px");
        mostRated.setWidth("300px");
        movieRateCount.setWidth("300px");
        actorRateCount.setWidth("300px");
        directorRateCount.setWidth("300px");
        commentCount.setWidth("300px");

        HorizontalLayout firstRow = new HorizontalLayout(avgRate, mostRated, commentCount);
        HorizontalLayout secondRow = new HorizontalLayout(movieRateCount, actorRateCount, directorRateCount);
        firstRow.setWidthFull();
        secondRow.setWidthFull();
        firstRow.getStyle().set("justify-content", "space-around");
        secondRow.getStyle().set("justify-content", "space-around");

        statistics.add(firstRow, secondRow);
        statistics.addClassName("details-layout");

        return statistics;
    }
    private VerticalLayout avgMovieRate(Long userId){
        VerticalLayout layout = new VerticalLayout();

        Double rate = userService.fetchAverageRate(userId);
        Div avgRate = new Div("Average movie rate");
        avgRate.addClassName("bold-component");
        Div text = new Div();
        if (rate == null || rate == 0.0){
            text.setText("No movies rated");
            layout.add(avgRate, text);
        }
        else {
            Icon star = starIcon();

            text.setText(rate >= 3.5 ? "You are amongst highest rated users!" : "You are amongst lowest rated users!");

            Span span = new Span(new Span(star), new Span(String.format("%.2f", rate)));
            Div div = new Div(span, text);

            layout.add(avgRate, div);
        }
        return layout;
    }
    private VerticalLayout mostRatedCategoryLayout(Long userId){
        VerticalLayout layout = new VerticalLayout();

        MovieCategory movieCategory = userService.fetchMostRatedCategory(userId);
        Div mostRated = new Div("Average movie rate");
        mostRated.addClassName("bold-component");
        if (movieCategory == null) {
            layout.add(mostRated, new Div("No movies rated"));
        }
        else {
            String category = movieCategory.toString().substring(0, 1).toUpperCase() + movieCategory.toString().substring(1).toLowerCase();

            layout.add(mostRated, new Div(category));
        }

        return layout;
    }
    private VerticalLayout movieRateCountLayout(Long userId){
        VerticalLayout layout = new VerticalLayout();

        Long count = userService.fetchMovieRateCount(userId);
        Div countDiv = new Div("Rated movies");
        countDiv.addClassName("bold-component");

        layout.add(countDiv, new Div(count.toString()));
        return layout;
    }
    private VerticalLayout actorRateCountLayout(Long userId){
        VerticalLayout layout = new VerticalLayout();

        Long count = userService.fetchActorRateCount(userId);
        Div countDiv = new Div("Rated actors");
        countDiv.addClassName("bold-component");

        layout.add(countDiv, new Div(count.toString()));
        return layout;
    }
    private VerticalLayout directorRateCountLayout(Long userId){
        VerticalLayout layout = new VerticalLayout();

        Long count = userService.fetchDirectorRateCount(userId);
        Div countDiv = new Div("Rated directors");
        countDiv.addClassName("bold-component");

        layout.add(countDiv, new Div(count.toString()));
        return layout;
    }
    private VerticalLayout commentCountLayout(UserEntity user){
        VerticalLayout layout = new VerticalLayout();

        int count = user.getComments().size();
        Div countDiv = new Div("Posted comments");
        countDiv.addClassName("bold-component");

        layout.add(countDiv, new Div(Integer.toString(count)));
        return layout;
    }
    private VerticalLayout personalDataLayout(UserEntity userEntity){
        VerticalLayout layout = new VerticalLayout();

        Div name = new Div(userEntity.getFirstName() + " " + userEntity.getLastName() + " (%s)".formatted(userEntity.getUsername()));
        name.addClassName("detail");

        LocalDate dateOfBirth = userEntity.getDateOfBirth();
        Div birthDate = new Div(dateOfBirth != null ? userEntity.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "Not set");
        birthDate.addClassName("detail");

        LocalDateTime registeredAt = userEntity.getRegisteredAt();
        String registered = registeredAt != null ? registeredAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh-mm")) : "Not set";
        Div registeredDiv = new Div("Registered: " + registered);
        registeredDiv.addClassName("detail");

        layout.add(name, birthDate, registeredDiv);
        return layout;
    }
    private VerticalLayout topRatedLayout(Long userId){
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("details-layout");

        Div header = new Div("Top rated");
        header.addClassName("detail");
        header.getStyle().set("align-self", "center");

        List<MovieDtoWithUserRate> movies = userService.fetchHighestRatedMoviesByUser(userId);
        List<ActorDtoWithUserRate> actors = userService.fetchHighestRatedActorsByUser(userId);
        List<DirectorDtoWithUserRate> directors = userService.fetchHighestRatedDirectorsByUser(userId);
        HorizontalLayout topRatedLayout = new HorizontalLayout();
        topRatedLayout.getStyle().set("margin", "auto").set("margin-bottom", "10px");
        renderTopRatedLayout(movies, topRatedLayout);

        configureTabs(movies, actors, directors, topRatedLayout, userId);

        layout.add(header, tabs, topRatedLayout);
        return layout;
    }
    private void renderTopRatedLayout(List<? extends EntityWithRate> list, HorizontalLayout topRatedLayout){
        topRatedLayout.removeAll();
        for(EntityWithRate entity : list){
            singleEntityLayout.addEntityToLayout(
                    entity,
                    topRatedLayout
            );
        }
    }
    private Button browseCommentsButton(String username){
        Button button = new Button("Browse user comments");
        button.addClickListener(e -> UI.getCurrent().navigate(UserCommentsView.class, username));
        button.getStyle().set("align-self", "center");
        return button;
    }
    private Div userNotFound(){
        Div notFound = new Div("User not found");
        notFound.getStyle().set("font-size", "24px").set("font-weight","bold");
        return notFound;
    }
    private void configureTabs(List<? extends EntityWithRate> movies, List<? extends EntityWithRate> actors,
                               List<? extends EntityWithRate> directors, HorizontalLayout top5Layout, Long userId){
        tabs.addSelectedChangeListener(e -> {
            if (e.getSelectedTab().equals(movieTab)){
                renderTopRatedLayout(movies, top5Layout);
            }
            else if (e.getSelectedTab().equals(actorTab)) {
                renderTopRatedLayout(actors, top5Layout);
            }
            else if (e.getSelectedTab().equals(directorTab)) {
                renderTopRatedLayout(directors, top5Layout);
            }
        });

        seeAllButton.addClickListener(ev -> {
           if (tabs.getSelectedTab().equals(movieTab)) UI.getCurrent().navigate(RatedMoviesView.class, userId);
           else if (tabs.getSelectedTab().equals(actorTab)) UI.getCurrent().navigate(RatedActorsView.class, userId);
           else UI.getCurrent().navigate(RatedDirectorsView.class, userId);
        });
    }
    private Icon starIcon(){
        Icon star = VaadinIcon.STAR.create();
        star.getStyle().set("font-size", "12px").set("margin-right", "14px").set("color", "blue");
        return star;
    }
}

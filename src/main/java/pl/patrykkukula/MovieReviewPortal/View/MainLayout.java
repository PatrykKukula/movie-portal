package pl.patrykkukula.MovieReviewPortal.View;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Layout;
import pl.patrykkukula.MovieReviewPortal.View.Account.AccountView;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorEditView;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorView;
import pl.patrykkukula.MovieReviewPortal.View.Actor.AddActorView;
import pl.patrykkukula.MovieReviewPortal.View.Director.AddDirectorView;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorEditView;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorView;
import pl.patrykkukula.MovieReviewPortal.View.Movie.MovieDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Movie.MovieView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Layout
@CssImport("./styles/main-layout.css")
public class MainLayout extends AppLayout {

    private final Div movies = new Div("Movies");
    private final Div actors = new Div("Actors");
    private final Div directors = new Div("Directors");
    private final Div account = new Div("My account");
    private final Map<Class<?>, Div> viewToTab = new HashMap<>();

    public MainLayout() {
        Div title = new Div("Movie Portal");
        title.setClassName("title");
        title.addClickListener(e -> UI.getCurrent().navigate(MovieView.class));
        setUpViewMap();
        setTabsClassNames();

        HorizontalLayout navBar = navBarLayout();
        HorizontalLayout container = new HorizontalLayout(title,navBar);

        container.addClassName("container");
        addToNavbar(container);
    }
    @Override
    protected void afterNavigation() {
        super.afterNavigation();

        viewToTab.values().forEach(tab -> tab.getStyle().remove("color"));

        Class<? extends Component> viewClass = getContent().getClass();
        Optional.ofNullable(viewToTab.get(viewClass)).ifPresent(view -> view.getStyle().set("color", "yellow"));
    }
    private void setTabsClassNames(){
        movies.setClassName("tab");
        directors.setClassName("tab");
        actors.setClassName("tab");
        account.setClassName("tab");
    }
    private HorizontalLayout navBarLayout(){
        HorizontalLayout leftTabs = new HorizontalLayout(movies, actors, directors);
        leftTabs.setSpacing(true);
        actors.addSingleClickListener(e -> UI.getCurrent().navigate(ActorView.class));
        directors.addSingleClickListener(e -> UI.getCurrent().navigate(DirectorView.class));
        account.addSingleClickListener(e -> UI.getCurrent().navigate(AccountView.class));
        movies.addSingleClickListener(e -> UI.getCurrent().navigate(MovieView.class));

        HorizontalLayout rightTabs = new HorizontalLayout(account);
        rightTabs.getStyle().set("margin-left", "auto");

        HorizontalLayout navBar = new HorizontalLayout(leftTabs, rightTabs);
        navBar.setWidthFull();
        navBar.setSpacing(true);
        navBar.setAlignItems(FlexComponent.Alignment.CENTER);

        return navBar;
    }
    private void setUpViewMap(){
        viewToTab.put(MovieView.class, movies);
        viewToTab.put(MovieDetailsView.class, movies);
        viewToTab.put(ActorView.class, actors);
        viewToTab.put(ActorDetailsView.class, actors);
        viewToTab.put(ActorEditView.class, actors);
        viewToTab.put(AddActorView.class, actors);
        viewToTab.put(DirectorView.class, directors);
        viewToTab.put(DirectorDetailsView.class, directors);
        viewToTab.put(DirectorEditView.class, directors);
        viewToTab.put(AddDirectorView.class, directors);
        viewToTab.put(AccountView.class, account);
    }
}

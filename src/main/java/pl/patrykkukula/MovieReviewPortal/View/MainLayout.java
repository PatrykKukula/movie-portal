package pl.patrykkukula.MovieReviewPortal.View;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.View.Account.AccountView;
import pl.patrykkukula.MovieReviewPortal.View.Account.LoginView;
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

@Slf4j
@Layout
@CssImport("./styles/main-layout.css")
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final Div movies = new Div("Movies");
    private final Div actors = new Div("Actors");
    private final Div directors = new Div("Directors");
    private final Div account = new Div("My account");
    private Html logoutForm;
    private final Div login = new Div("Login");
    private final Div details = new Div("Details");
    private final Div detailsContainer = new Div();
    private final Map<Class<?>, Div> viewToTab = new HashMap<>();
    private final IAuthService authService;
    private final UserDetailsServiceImpl userDetailsService;

    public MainLayout(IAuthService authService, UserDetailsServiceImpl userDetailsService1) {
        this.authService = authService;
        this.userDetailsService = userDetailsService1;
        Div title = new Div("Movie Portal");
        title.setClassName("title");
        title.addClickListener(e -> UI.getCurrent().navigate(MovieView.class));
        setupLogout();
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
        account.setClassName("account-tab");
        login.setClassName("tab");
        details.setClassName("dropdown-tab");
        detailsContainer.setClassName("tabs-container");

    }
    private HorizontalLayout navBarLayout(){
        HorizontalLayout leftTabs = new HorizontalLayout(movies, actors, directors);
        leftTabs.setSpacing(true);
        actors.addSingleClickListener(e -> UI.getCurrent().navigate(ActorView.class));
        directors.addSingleClickListener(e -> UI.getCurrent().navigate(DirectorView.class));
        movies.addSingleClickListener(e -> UI.getCurrent().navigate(MovieView.class));
        login.addSingleClickListener(e -> UI.getCurrent().navigate(LoginView.class));

        HorizontalLayout navBar = new HorizontalLayout(leftTabs);
        detailsContainer.add(logoutForm,details);
        account.add(detailsContainer);
        HorizontalLayout rightTabs = new HorizontalLayout();
        rightTabs.getStyle().set("margin-left", "auto").set("padding-right", "1rem");

        if (userDetailsService.getAuthenticatedUser()!=null) {
            rightTabs.add(account);
        }
        else {
            rightTabs.add(login);
        }
        navBar.add(rightTabs);
        navBar.setWidthFull();
        navBar.setSpacing(true);
        navBar.setAlignItems(FlexComponent.Alignment.CENTER);

        return navBar;
    }
    private void setupLogout(){
        CsrfToken csrf = (CsrfToken) VaadinServletRequest.getCurrent().getAttribute(CsrfToken.class.getName());
        logoutForm = new Html("""
                <form id="logoutForm" method="post" action="/logout">
                    <input type="hidden" name="%s" value="%s"/>
                    <button type="submit" style="all: unset;">Logout</button>
                </form>
                """.formatted(csrf.getParameterName(), csrf.getToken()));
        logoutForm.setClassName("dropdown-tab");
    }
    private void setUpViewMap() {
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
        viewToTab.put(LoginView.class, login);
//        viewToTab.put(AccountDetails.view, details);
    }
}

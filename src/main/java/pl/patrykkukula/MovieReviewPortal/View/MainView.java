package pl.patrykkukula.MovieReviewPortal.View;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route
@PageTitle("Movie Portal")
@AnonymousAllowed
public class MainView extends VerticalLayout {

    public MainView() {

        add(new H1("Hello"));
    }
}

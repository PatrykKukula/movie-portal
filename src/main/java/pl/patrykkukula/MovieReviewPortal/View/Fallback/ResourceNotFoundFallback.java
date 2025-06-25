package pl.patrykkukula.MovieReviewPortal.View.Fallback;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.NoArgsConstructor;

@Route("not-found")
@PageTitle("Not found")
@NoArgsConstructor
@AnonymousAllowed
public class ResourceNotFoundFallback extends HorizontalLayout implements HasUrlParameter<String> {

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        removeAll();
        H3 errorMessage = new H3(parameter);
        add(errorMessage);
    }
}

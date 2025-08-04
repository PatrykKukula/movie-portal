package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("user")
@PageTitle("Profile details")
@AnonymousAllowed
public class ProfileView extends HorizontalLayout implements HasUrlParameter<Long> {
    @Override
    public void setParameter(BeforeEvent event, Long parameter) {

    }
}

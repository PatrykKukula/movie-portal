package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("account")
@RolesAllowed({"USER", "ADMIN"})
public class AccountView extends HorizontalLayout {
}

package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;

@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends Composite<FormLayout> {
        private final AuthServiceImpl authService;

    public RegisterView(AuthServiceImpl authService) {
        this.authService = authService;








    }
}

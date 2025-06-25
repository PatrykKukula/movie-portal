package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;

@Slf4j
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();
    private final IAuthService authService;

    public LoginView(IAuthService authService) {
        this.authService = authService;
        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
        errorMessage.setMessage("Wrong username or password");
        errorMessage.setTitle("Failed to log in");
        errorMessage.setUsername("Email cannot be empty");
        errorMessage.setPassword("Password cannot be empty");

        LoginI18n.Form form = i18n.getForm();
        form.setUsername("Email");
        i18n.setErrorMessage(errorMessage);
        i18n.setForm(form);

        loginForm.setI18n(i18n);
        loginForm.setAction("login");

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(loginForm);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")){
            loginForm.setError(true);
        }
    }
}

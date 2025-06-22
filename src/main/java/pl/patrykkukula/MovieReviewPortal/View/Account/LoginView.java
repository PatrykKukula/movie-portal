package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    public LoginView() {

        LoginForm loginForm = new LoginForm();
        LoginI18n i18n = LoginI18n.createDefault();

        LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
        errorMessage.setMessage("Error message");
        errorMessage.setTitle("Error title");
        errorMessage.setUsername("Username");
        errorMessage.setPassword("Password");

        i18n.setErrorMessage(errorMessage);

        loginForm.setI18n(i18n);

        loginForm.addLoginListener(e -> {
           loginForm.showErrorMessage(i18n.getHeader().getTitle(), i18n.getErrorMessage().getMessage());
        });

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(loginForm);
    }
}

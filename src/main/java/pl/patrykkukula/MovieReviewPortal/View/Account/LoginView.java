package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.UserServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.AccountViewConstants.*;

@Slf4j
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final LoginForm loginForm = new LoginForm();
    private final IAuthService authService;
    private final Dialog resetDialog = new Dialog();
    private final Dialog formDialog = new Dialog();
    private final UserDetailsServiceImpl userDetailsService;
    private final UserServiceImpl userService;

    public LoginView(IAuthService authService, UserDetailsServiceImpl userDetailsService, UserServiceImpl userService) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        LoginI18n i18n = LoginI18n.createDefault();
        loginForm.setForgotPasswordButtonVisible(true);
        loginForm.addForgotPasswordListener(new forgotPasswordEventListener());

        LoginI18n.Form form = i18n.getForm();
        form.setUsername("Email");

        i18n.setErrorMessage(errorMessage(i18n));
        i18n.setForm(form);

        loginForm.setI18n(i18n);
//        loginForm.setAction("login");
        loginForm.addLoginListener(e -> {
            try {
                UserEntity user = userService.loadUserEntityByEmail(e.getUsername());
                if (!user.isEnabled()) {
                    formDialog.removeAll();
                    Div error = new Div("Account is not activated");
                    formDialog.add(error);
                    formDialog.open();
                    // add link
                } else {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    UI.getCurrent().getPage().reload();
                }
            }
            catch (UsernameNotFoundException | NullPointerException ex){
                loginForm.setError(true);
            }
        });
        loginForm.getStyle().set("border", "1px solid red");

        VerticalLayout loginLayout = new VerticalLayout();
        loginLayout.add(loginForm, registerButton());
        loginLayout.getStyle().set("align-items", "center");

        add(loginLayout);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (userDetailsService.getAuthenticatedUser() != null){
            event.forwardTo(AccountView.class);
        }
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        resetDialog.close();
        formDialog.close();
    }
    private Button registerButton(  ){
        Button registerButton = new Button(REGISTER);
        registerButton.addClickListener(e -> UI.getCurrent().navigate(RegisterView.class));
        registerButton.getStyle().set("padding", "10px");
        return registerButton;
    }
    private Button sendTokenButton(TextField emailField){
        Button sendToken = new Button("Send token");
        sendToken.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendToken.addClickListener(e ->{
            try {
                resetDialog.removeAll();
                String token = authService.generatePasswordResetToken(emailField.getValue());
                String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
                String url = "/reset?token=" + encodedToken;
                Anchor resetLink = new Anchor(url, CLICK_TO_RESET);
                Div div = resetDialogLayout(resetLink);
                resetDialog.add(div);
                resetDialog.open();
            }
            catch (ResourceNotFoundException ex){
                Dialog dialog = new Dialog();
                Div errorMessage = new Div(ex.getMessage());
                dialog.add(errorMessage);
                dialog.open();
            }
     });
        return sendToken;
    }
    private LoginI18n.ErrorMessage errorMessage(LoginI18n i18n){
        LoginI18n.ErrorMessage errorMessage = i18n.getErrorMessage();
        errorMessage.setMessage("Wrong username or password");
        errorMessage.setTitle("Failed to log in");
        errorMessage.setUsername("Email cannot be empty");
        errorMessage.setPassword("Password cannot be empty");
        return errorMessage;
    }
    private Div resetDialogLayout(Anchor resetLink){
        Div div = new Div();
        Div resetText = new Div(RESET_LINK_TEXT);
        resetText.getStyle().set("font-weight", "bold");
        div.add(resetText, resetLink);
        div.getStyle().set("text-align", "center");
        return div;
    }
    class forgotPasswordEventListener implements ComponentEventListener<AbstractLogin.ForgotPasswordEvent> {
        @Override
        public void onComponentEvent(AbstractLogin.ForgotPasswordEvent event) {
            formDialog.removeAll();
            Div text = new Div(RESET_PASSWORD_TEXT);
            text.getStyle().set("font-weight", "bold");

            TextField emailField = FormFields.textField("Email");
            emailField.setWidthFull();

            VerticalLayout layout = new VerticalLayout();
            layout.add(text, emailField, sendTokenButton(emailField));
            layout.setAlignItems(Alignment.CENTER);

            formDialog.add(layout);
            formDialog.open();
        }
    }
}

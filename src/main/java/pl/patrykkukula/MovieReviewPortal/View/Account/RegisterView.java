package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.UserEntityDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.UserSexComboBox;
import pl.patrykkukula.MovieReviewPortal.View.Movie.MovieView;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.View.Account.AccountViewConstants.*;

@Route("register")
@PageTitle("Register")
@AnonymousAllowed
public class RegisterView extends Composite<FormLayout> implements BeforeLeaveObserver {
        private final AuthServiceImpl authService;
        private final Dialog verifyDialog = new Dialog();

    public RegisterView(AuthServiceImpl authService) {
        this.authService = authService;
        FormLayout layout = getContent();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        layout.getStyle().set("margin", "auto").set("width", "20%");
        BeanValidationBinder<UserEntityDto> binder = new BeanValidationBinder<>(UserEntityDto.class);

        UserEntityDto userDto = new UserEntityDto();

        TextField usernameField = FormFields.textField("Username");
        binder.bind(usernameField, "username");
        TextField emailField = FormFields.textField("Email");
        binder.bind(emailField, "email");
        PasswordField passwordField = FormFields.passwordField();
        binder.bind(passwordField, "password");
        ComboBox<UserSex> userSexComboBox = new UserSexComboBox();
        binder.bind(userSexComboBox, "userSex");

        binder.setBean(userDto);
        Button registerButton = registerButton(binder);
        Button cancelButton = Buttons.cancelButton(MovieView.class);

        layout.add(usernameField, emailField, passwordField, userSexComboBox, registerButton, cancelButton);
    }
    private Button registerButton(BeanValidationBinder<UserEntityDto> binder) {
        Button registerButton = new Button("Register");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            registerButton.addClickListener(e -> {
                try {
                    verifyDialog.removeAll();
                    if (binder.isValid()) {
                        UserEntityDto dto = binder.getBean();
                        String verificationToken = authService.register(dto);
                        String encodedToken = URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);
                        String url = "/verify?token=" + encodedToken;
                        Anchor verifyLink = new Anchor(url, "Click to verify account");
                        Div div = verifyDialogLayout(verifyLink);
                        verifyDialog.add(div);
                        verifyDialog.open();
                    } else {
                        List<ValidationResult> validationErrors = binder.validate().getValidationErrors();
                        Dialog dialog = CommonComponents.validationErrorsDialog(validationErrors);
                        dialog.open();
                    }
                } catch (IllegalStateException ex) {
                    Div error = new Div(ex.getMessage());
                    verifyDialog.add(error);
                    verifyDialog.open();
                }
            });
        return registerButton;
    }
    private Div verifyDialogLayout(Anchor verifyLink){
        Div div = new Div();
        Div verifyText = new Div(VERIFY_ACCOUNT_TEXT);
        div.add(verifyText, verifyLink);
        div.getStyle().set("text-align", "center");
        return div;
    }
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        verifyDialog.close();
    }
}

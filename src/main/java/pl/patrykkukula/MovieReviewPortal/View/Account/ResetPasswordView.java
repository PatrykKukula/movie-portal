package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;
import com.vaadin.flow.component.button.Button;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.MainView;

import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.View.Account.AccountViewConstants.*;

@Route("reset")
@PageTitle("Reset password")
@AnonymousAllowed
public class ResetPasswordView extends Composite<FormLayout> implements BeforeEnterObserver, BeforeLeaveObserver {
    private final AuthServiceImpl authService;
    private final BeanValidationBinder<PasswordResetDto> binder = new BeanValidationBinder<>(PasswordResetDto.class);
    private final Dialog resetDialog = new Dialog();

    public ResetPasswordView(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        PasswordResetDto dto = new PasswordResetDto();
        binder.setBean(dto);
        FormLayout layout = getContent();

        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        layout.getStyle().set("margin", "auto").set("width","20%").set("align-items", "center");
        PasswordField password = new PasswordField("Enter new password");
        password.getStyle().set("margin-bottom", "10px");
        binder.bind(password, "newPassword");

        String token = event.getLocation().getQueryParameters().getParameters().getOrDefault("token", List.of("")).getFirst();

        Button changePasswordButton = changePasswordButton(token);
        Button cancelButton = Buttons.cancelButton(LoginView.class);

        layout.add(password, changePasswordButton, cancelButton);
    }
    private Button changePasswordButton(String token){
        Button button = new Button("Change password");
        button.getStyle().set("margin-bottom", "10px");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            try {
                resetDialog.removeAll();
                PasswordResetDto resetDto = binder.getBean();
                resetDto.setPwdResetToken(token);
                authService.resetPassword(resetDto);
                Div success = new Div(PASSWORD_RESET_SUCCESS);
                resetDialog.add(success);
                resetDialog.open();
                UI.getCurrent().navigate(MainView.class);
            }
            catch (ResourceNotFoundException | IllegalStateException ex) {
                Span failure = new Span(PASSWORD_RESET_FAILED);
                failure.getStyle().set("font-weight", "bold");
                Span errorMessage = new Span(ex.getMessage());
                Div error = new Div();
                error.getStyle().set("white-space", "pre-line").set("align-items", "center");
                error.add(failure, errorMessage);
                resetDialog.add(error);
                resetDialog.open();
            }
        });
        return button;
    }
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        resetDialog.close();
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.MainView;

import java.util.List;
import java.util.stream.Collectors;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.AccountViewConstants.PASSWORD_RESET_FAILED;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.AccountViewConstants.PASSWORD_RESET_SUCCESS;

@Slf4j
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
        password.setValueChangeMode(ValueChangeMode.EAGER);
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

        PasswordResetDto resetDto = binder.getBean();
        button.addClickListener(e -> {
            resetDialog.removeAll();
            if (binder.writeBeanIfValid(resetDto)) {
                try {
                    resetDto.setPwdResetToken(token);
                    authService.resetPassword(resetDto);
                    resetDialog.add(PASSWORD_RESET_SUCCESS);
                    resetDialog.open();
                    UI.getCurrent().navigate(MainView.class);
                } catch (ResourceNotFoundException | IllegalStateException ex) {
                    Span failure = new Span(PASSWORD_RESET_FAILED);
                    failure.getStyle().set("font-weight", "bold").set("font-weight", "bold");;
                    Span errorMessage = new Span(ex.getMessage());
                    Div error = new Div();
                    error.getStyle().set("white-space", "pre-line").set("text-align", "center");
                    error.add(failure, errorMessage);
                    resetDialog.add(error);
                    resetDialog.open();
                }
            }
            else {
                Div error = new Div(binder.validate().getValidationErrors()
                        .stream().map(ValidationResult::getErrorMessage)
                        .collect(Collectors.joining("\n")));
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

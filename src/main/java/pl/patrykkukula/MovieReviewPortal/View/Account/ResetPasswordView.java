package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.UserRelated.PasswordResetDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;

import java.util.List;

@Route("reset")
@PageTitle("reset password")
@AnonymousAllowed
public class ResetPasswordView extends FormLayout implements BeforeEnterObserver {
    private final AuthServiceImpl authService;
    private final PasswordField newPasswordField = new PasswordField("New password");
    private final PasswordField confirmPasswordField = new PasswordField("Confirm password");
    private final BeanValidationBinder<PasswordResetDto> binder = new BeanValidationBinder<>(PasswordResetDto.class);
    private final PasswordResetDto passwordResetDto = new PasswordResetDto();
    private Dialog validationDialog;

    public ResetPasswordView(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        addClassName("main-layout");
        getStyle().set("align-items", "center");
        setResponsiveSteps(new ResponsiveStep("0", 1, ResponsiveStep.LabelsPosition.TOP));

        binder.setBean(passwordResetDto);
        binder.bind(newPasswordField, "newPassword");
        binder.forField(confirmPasswordField).bind("confirmPassword");
        Button confirmButton = confirmButton(event);

        add(newPasswordField, confirmPasswordField, confirmButton);
    }
    private Button confirmButton(BeforeEnterEvent event){
        Button confirm = new Button("Confirm password reset");
        confirm.addClickListener(e -> {
            try{
                String token = event.getLocation().getQueryParameters().getParameters("token").getFirst();
                if (binder.validate().isOk()) {
                    PasswordResetDto bean = binder.getBean();
                    boolean success = authService.resetPassword(
                            PasswordResetDto.builder()
                                    .pwdResetToken(token)
                                    .newPassword(bean.getNewPassword())
                                    .confirmPassword(bean.getConfirmPassword())
                                    .build()
                    );
                    if (success) {
                        Notification.show("Password reset successfully", 3000, Notification.Position.MIDDLE);
                        UI.getCurrent().navigate(LoginView.class);
                    }
                    else Notification.show("Something went wrong. Please try again", 5000, Notification.Position.MIDDLE);
                }
                else {
                    List<ValidationResult> validationResults = binder.validate().getValidationErrors();
                    validationDialog = pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents.validationErrorsDialog(validationResults);
                    validationDialog.open();
                }
            }
            catch (IllegalStateException ex){
                Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return confirm;
    }
}

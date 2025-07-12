package pl.patrykkukula.MovieReviewPortal.View.Account;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.Service.IAvatarService;
import pl.patrykkukula.MovieReviewPortal.View.Common.AvatarImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;

import java.io.IOException;
import java.util.Optional;

@Route("account")
@RolesAllowed({"USER", "ADMIN"})
@CssImport("./styles/common-styles.css")
public class AccountView extends VerticalLayout {

    private final UserDetailsServiceImpl userDetailsService;
    private final IAuthService authService;
    private final IAvatarService avatarService;
    private final Span email = new Span();
    private final Dialog dialog = new Dialog();
    private final Span emailValue = new Span();
    private static final int MAX_SIZE_BYTES = 2 * 1024 * 1024;
    private static final String MAX_SIZE_MB = "2";
    private static final String[] ALLOWED_TYPES = new String[]{"image/png","image/jpg","image/jpeg"};
    private static final String ALLOWED_FORMAT = ".png, .jpg, .jpeg";
    private static final String AVATAR_WIDTH = "100px";
    private static final String AVATAR_HEIGHT = "100px";

    public AccountView(UserDetailsServiceImpl userDetailsService, IAuthService authService, UserEntityRepository userRepository, IAvatarService avatarService) {
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.avatarService = avatarService;
        setSizeFull();

        Span dialogSuccess = new Span("Reset successfully");
        dialog.add(dialogSuccess);

        Div upload = setUploadDiv();

        UserDetails authenticatedUser = userDetailsService.getAuthenticatedUser();
        if (authenticatedUser != null) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(authenticatedUser.getUsername());
            if (optionalUser.isEmpty()) {
                UI.getCurrent().navigate(LoginView.class);
                return;
            }
            UserEntity user = optionalUser.get();

            AvatarImpl avatar = new AvatarImpl(avatarService, userDetailsService, AVATAR_WIDTH, AVATAR_HEIGHT);
            Span username = usernameSpan(user);
            emailSpan(user);
            Div password = passwordDiv();
            FormLayout passwordForm = passwordForm(user);
            FormLayout emailForm = emailForm(user, userRepository);

            password.add(editIcon(passwordForm, "Change password"), passwordForm);
            email.add(new Span(editIcon(emailForm, "Change email"), emailForm));

            add(avatar, username, email, password, upload);
        }
        else UI.getCurrent().navigate(LoginView.class);
    }
    private Icon editIcon(FormLayout form, String tooltip){
        Icon icon = VaadinIcon.EDIT.create();
        icon.addClickListener(e -> form.setVisible(true));
        icon.addClassName("icon");
        icon.setTooltipText(tooltip);
        return icon;
    }
    private FormLayout passwordForm(UserEntity user){
        FormLayout passwordForm = new FormLayout();
        passwordForm.setVisible(false);
        PasswordField password = getPasswordField();

        Button change = new Button("Change password");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClickListener(e -> {
            boolean isChanged = authService.changePassword(user, password.getValue());
            if (isChanged) {
                passwordForm.setVisible(false);
                dialog.open();
            }
        });
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> passwordForm.setVisible(false));

        passwordForm.add(password, change, cancel);
        return passwordForm;
    }
    private FormLayout emailForm(UserEntity user, UserEntityRepository userRepository){
        FormLayout emailForm = new FormLayout();
        emailForm.setVisible(false);
        TextField emailField = getEmailField();
        emailField.setValue(user.getEmail());

        Button change = new Button("Change email");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClickListener(e -> {
            boolean isChanged = authService.changeEmail(user, emailField.getValue());
            if (isChanged) {
                    emailForm.setVisible(false);
                    dialog.open();

                    Optional<UserEntity> updatedUserOpt = userRepository.findById(user.getUserId());
                    if (updatedUserOpt.isPresent()) {
                        emailValue.removeAll();
                        String updatedEmail = updatedUserOpt.get().getEmail();
                        emailValue.setText(updatedEmail);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(updatedEmail);
                        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
            }
        });
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> emailForm.setVisible(false));

        emailForm.add(emailField, change, cancel);
        return emailForm;
    }
    private static PasswordField getPasswordField() {
        PasswordField password = new PasswordField("New password");
        password.setPattern(GlobalConstants.PASSWORD_REGEX);
        password.setValueChangeMode(ValueChangeMode.EAGER);

        PasswordField.PasswordFieldI18n passwordI18n = new PasswordField.PasswordFieldI18n();
        passwordI18n.setPatternErrorMessage("Password must contain small and capital letter, number, special character and be at lest 8 character long");
        password.setI18n(passwordI18n);
        return password;
    }
    private Div setUploadDiv(){
        Div uploadAvatar = new Div();
        Upload upload = getUpload();

        Icon closeIcon = CommonComponents.closeIcon();

        Paragraph hint = new Paragraph("Maximum file size: " + MAX_SIZE_MB + "MB");
        Paragraph allowedFormat = new Paragraph("Allowed files types: " + ALLOWED_FORMAT);
        HorizontalLayout uploadLayout = new HorizontalLayout(upload, closeIcon);
        Div uploadDiv = new Div(uploadLayout, hint, allowedFormat);
        uploadDiv.setVisible(false);

        Button uploadButton = new Button("Upload avatar");
        uploadButton.addClickListener(e -> {
            uploadDiv.setVisible(true);
            uploadButton.setVisible(false);
        });
        closeIcon.addClickListener(e -> {
            uploadButton.setVisible(true);
            uploadDiv.setVisible(false);
        });
        uploadAvatar.add(uploadButton, uploadDiv);
        return uploadAvatar;
    }
    private Upload getUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);

        UploadI18N I18N = new UploadI18N();
        UploadI18N.Error error = new UploadI18N.Error();
        error.setFileIsTooBig("File cannot exceed 2MB");
        error.setIncorrectFileType("Allowed file types: " + ALLOWED_FORMAT);
        I18N.setError(error);

        upload.setI18n(I18N);
        upload.setAcceptedFileTypes(ALLOWED_TYPES);
        upload.setMaxFileSize(MAX_SIZE_BYTES);
        upload.addSucceededListener(e ->{
            String userId = String.valueOf(userDetailsService.getAuthenticatedUserId());
            try {
                avatarService.saveAvatar(userId, e.getMIMEType().substring(6), buffer.getInputStream(), MAX_SIZE_BYTES);
            } catch (IOException ex) {
                String errorMessage = ex.getMessage();
                Notification notification = Notification.show(errorMessage, 2500,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        upload.addFileRejectedListener(e ->{
            String errorMessage = e.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 2500,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        return upload;
    }
    private static TextField getEmailField() {
        TextField email = new TextField("New email");
        email.setPattern(GlobalConstants.EMAIL_REGEX);
        email.setValueChangeMode(ValueChangeMode.EAGER);
        email.setErrorMessage("Invalid email address format");
        return email;
    }
    private Span usernameSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Username: ");
        Span usernameSpan = new Span(label);
        usernameSpan.add(user.getUsername());
        return usernameSpan;
    }
    private void emailSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Email: ");
        email.add(label);
        emailValue.setText(user.getEmail());
        email.add(emailValue);
    }
    private Div passwordDiv(){
        Span label = CommonComponents.labelSpan("Password: ");
        Span span = new Span("********");
        return new Div(label, span);
    }
}

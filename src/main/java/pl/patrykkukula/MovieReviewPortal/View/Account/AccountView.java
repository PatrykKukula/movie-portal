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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IAuthService;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.EditFieldForm;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.AvatarImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Account.Components.UserSexComboBox;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.CustomDatePicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.Constants.GlobalConstants.*;
import static pl.patrykkukula.MovieReviewPortal.View.Account.AccountViewConstants.*;

@Slf4j
@Route("account")
@RolesAllowed({"USER", "ADMIN"})
@CssImport("./styles/account-styles.css")
@CssImport("./styles/common-styles.css")
public class AccountView extends VerticalLayout {
    private final UserDetailsServiceImpl userDetailsService;
    private final IAuthService authService;
    private final IImageService avatarService;
    private final Span email = new Span();
    private final Span emailValue = new Span();
    private final Span sex = new Span();
    private final Span sexValue = new Span();
    private final Span firstName = new Span();
    private final Span firstNameValue = new Span();
    private final Span lastName = new Span();
    private final Span lastNameValue = new Span();
    private final Span dateOfBirth = new Span();
    private final Span dateOfBirthValue = new Span();
    private final Dialog dialog = new Dialog();
    private final Span dialogText = new Span();

    public AccountView(UserDetailsServiceImpl userDetailsService, IAuthService authService, UserEntityRepository userRepository, IImageService avatarService) throws IOException {
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.avatarService = avatarService;
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("margin", "auto").set("display", "flex");
        dialog.add(dialogText);

        Div upload = setUploadDiv();

        UserDetails authenticatedUser = userDetailsService.getAuthenticatedUser();
        if (authenticatedUser != null) {
            Optional<UserEntity> optionalUser = userRepository.findByEmail(authenticatedUser.getUsername());
            if (optionalUser.isEmpty()) {
                UI.getCurrent().navigate(LoginView.class);
                return;
            }
            UserEntity user = optionalUser.get();

            AvatarImpl avatar = new AvatarImpl(avatarService, userDetailsService, AVATAR_WIDTH, AVATAR_HEIGHT, DIR, DIR_PH);
            Span username = usernameSpan(user);
            Div password = passwordDiv();
            sexSpan(user);
            emailSpan(user);
            firstNameSpan(user);
            lastNameSpan(user);
            dateOfBirthSpan(user);

            FormLayout passwordForm = passwordForm(user);
            FormLayout emailForm = new EditFieldForm(
                    user, userRepository, userDetailsService,
                    EMAIL_REGEX, "Invalid email form", "New email", "Change email",
                    dialogText, "Email changed successfully", dialog, emailValue,
                    () -> user.getEmail(),
                    (userEntity, fieldValue) -> authService.changeEmail(userEntity, fieldValue), true
            );
            FormLayout firstNameForm = new EditFieldForm(
                    user, userRepository, userDetailsService,
                    ONLY_LETTER_REGEX, "Name must contain only letters", "New first name", "Change first name",
                    dialogText, "First name changed successfully", dialog, firstNameValue,
                    () -> user.getFirstName(),
                    (userEntity, fieldValue) -> authService.changeFirstName(userEntity, fieldValue), false
            );
            FormLayout lastNameForm = new EditFieldForm(
                    user, userRepository, userDetailsService,
                    ONLY_LETTER_REGEX, "Last name must contain only letters", "New last name", "Change last name",
                    dialogText, "Last name changed successfully", dialog, lastNameValue,
                    () -> user.getLastName(),
                    (userEntity, fieldValue) -> authService.changeLastName(userEntity, fieldValue), false
            );
            FormLayout sexForm = userSexLayout(user, userRepository);
            FormLayout dateOfBirthForm = dateOfBirthForm(user);

            email.add(editIcon(emailForm, "Change email"));
            VerticalLayout emailFormLayout = new VerticalLayout(email, emailForm);
            emailFormLayout.addClassName("detail-layout");

            firstName.add(editIcon(firstNameForm, "Change first name"), firstNameForm);
            VerticalLayout firstNameFormLayout = new VerticalLayout(firstName, firstNameForm);
            firstNameFormLayout.addClassName("detail-layout");

            lastName.add(editIcon(lastNameForm, "Change last name"), lastNameForm);
            VerticalLayout lastNameFormLayout = new VerticalLayout(lastName, lastNameForm);
            lastNameFormLayout.addClassName("detail-layout");

            password.add(editIcon(passwordForm, "Change password"), passwordForm);
            VerticalLayout passwordFormLayout = new VerticalLayout(password, passwordForm);
            passwordFormLayout.addClassName("detail-layout");

            sex.add(editIcon(sexForm, "Change sex"), sexForm);
            VerticalLayout sexFormLayout = new VerticalLayout(sex, sexForm);
            sexFormLayout.addClassName("detail-layout");

            dateOfBirth.add(editIcon(dateOfBirthForm, "Change date of birth"), dateOfBirthForm);
            VerticalLayout dateOfBirthFormLayout = new VerticalLayout(dateOfBirth, dateOfBirthForm);
            dateOfBirthFormLayout.addClassName("detail-layout");

            Div personalInfoHeader = new Div("Personal information");
            personalInfoHeader.addClassName("bold-component");
            VerticalLayout personalInfo = new VerticalLayout(personalInfoHeader, firstNameFormLayout, lastNameFormLayout, dateOfBirthFormLayout, sexFormLayout);
            personalInfo.addClassName("centered-bordered-layout");

            Div accountDetailsInfoHeader = new Div("Account details");
            accountDetailsInfoHeader.addClassName("bold-component");
            VerticalLayout accountDetailsInfo = new VerticalLayout(accountDetailsInfoHeader, username, emailFormLayout, passwordFormLayout, upload);
            accountDetailsInfo.addClassName("centered-bordered-layout");

            add(avatar, personalInfo, accountDetailsInfo);
        }
        else UI.getCurrent().navigate(LoginView.class);
    }
    private FormLayout passwordForm(UserEntity user){
        FormLayout passwordForm = new FormLayout();
        passwordForm.setVisible(false);
        passwordForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        passwordForm.addClassName("form-layout");
        PasswordField password = getPasswordField();

        Button change = new Button("Change password");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClickListener(e -> {
            boolean isChanged = authService.changePassword(user, password.getValue());
            if (isChanged) {
                passwordForm.setVisible(false);
                dialogText.removeAll();
                dialogText.setText("Password changed successfully");
                dialog.open();
            }
        });
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> passwordForm.setVisible(false));

        passwordForm.add(password, change, cancel);
        return passwordForm;
    }
    private FormLayout userSexLayout(UserEntity user, UserEntityRepository userRepository) {
        UserSexComboBox userSexComboBox = new UserSexComboBox();

        FormLayout sexLayout = new FormLayout();
        sexLayout.setVisible(false);
        sexLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        sexLayout.addClassName("form-layout");

        Button change = new Button("Change sex");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClickListener(e -> {
            boolean isChanged = authService.changeSex(user, userSexComboBox.getValue());
            if (isChanged) {
                dialogText.removeAll();
                dialogText.setText("Sex changed successfully");
                dialog.open();
                sexLayout.setVisible(false);

                Optional<UserEntity> updatedUserOpt = userRepository.findById(user.getUserId());
                if (updatedUserOpt.isPresent()) {
                    sexValue.removeAll();
                    UserSex updatedSex = updatedUserOpt.get().getUserSex();
                    sexValue.setText(updatedSex.toString());
                }
            }
        });
        Button cancel = new Button("Cancel");

        sexLayout.add(userSexComboBox, change, cancel);

        cancel.addClickListener(e -> {
            cancel.setVisible(false);
            sexLayout.setVisible(false);
        });

        return sexLayout;
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
    private FormLayout dateOfBirthForm(UserEntity user){
        FormLayout dateOfBirthForm = new FormLayout();
        dateOfBirthForm.setVisible(false);

        CustomDatePicker datePicker = new CustomDatePicker();
        FormLayout datePickerLayout = datePicker.generateDatePickerLayout("Pick date of birth");
        datePicker.setErrorMessage("Date cannot be in the future");
        datePicker.setPresentationValue(LocalDate.now());
        datePicker.addValueChangeListener(e -> {
            if (e.getValue() == null && e.getValue().isAfter(LocalDate.now())) {
                datePicker.setErrorMessage("Date must be in the past");
                datePicker.setInvalid(true);
            } else {
                datePicker.setInvalid(false);
                datePicker.setErrorMessage(null);
            }
        });
        Button change = new Button("Change date of birth");
        change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        change.addClickListener(e -> {
            if (datePicker.getValue().isBefore(LocalDate.now())) {
                boolean isChanged = authService.changeDateOfBirth(user, datePicker.getValue());
                if (isChanged) {
                    datePicker.setInvalid(false);
                    dateOfBirthValue.removeAll();
                    dateOfBirthValue.setText(datePicker.getValue().toString());
                    dateOfBirthForm.setVisible(false);
                    dialogText.removeAll();
                    dialogText.setText("Date of birth changed successfully");
                    dialog.open();
                }
            }
        });
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> dateOfBirthForm.setVisible(false));

        dateOfBirthForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        datePickerLayout.add(change, cancel);
        dateOfBirthForm.add(datePickerLayout);
        dateOfBirthForm.addClassName("form-layout");
        dateOfBirthForm.getStyle().set("width","24%");
        return dateOfBirthForm;
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
                avatarService.saveImage(userId, MAX_SIZE_BYTES, e.getMIMEType().substring(6), DIR, buffer.getInputStream());
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
    private static PasswordField getPasswordField() {
        PasswordField password = new PasswordField("New password");
        password.setPattern(PASSWORD_REGEX);
        password.setValueChangeMode(ValueChangeMode.EAGER);

        PasswordField.PasswordFieldI18n passwordI18n = new PasswordField.PasswordFieldI18n();
        passwordI18n.setPatternErrorMessage("Password must contain small and capital letter, number, special character and be at lest 8 character long");
        password.setI18n(passwordI18n);
        return password;
    }
    private void emailSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Email: ");
        email.add(label);
        emailValue.setText(user.getEmail());
        email.add(emailValue);
    }
    private void firstNameSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("First name: ");
        firstName.add(label);
        if (user.getFirstName() != null) {
            firstNameValue.setText(user.getFirstName());
        }
        else firstNameValue.setText("");
        firstName.add(firstNameValue);
    }
    private void lastNameSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Last name: ");
        lastName.add(label);
        if (user.getLastName() != null) {
            lastNameValue.setText(user.getLastName());
        }
        else lastNameValue.setText("");
        lastName.add(lastNameValue);
    }
    private void dateOfBirthSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Date of birth: ");
        dateOfBirth.add(label);
        if (user.getDateOfBirth() != null) {
            dateOfBirthValue.setText(user.getDateOfBirth().toString());
        }
        else dateOfBirthValue.setText("");
        dateOfBirth.add(dateOfBirthValue);
    }
    private Span usernameSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Username: ");
        Span usernameSpan = new Span(label);
        usernameSpan.add(user.getUsername());
        return usernameSpan;
    }
    private Div passwordDiv(){
        Span label = CommonComponents.labelSpan("Password: ");
        Span span = new Span("********");
        return new Div(label, span);
    }
    private void sexSpan(UserEntity user){
        Span label = CommonComponents.labelSpan("Sex: ");
        sex.add(label);
        UserSex userSex = user.getUserSex();
        if (userSex != null) {
            sexValue.setText(userSex.toString());
        } else sexValue.setText("unknown");
        sex.add(sexValue);
    }
    private Span editIcon(FormLayout form, String tooltip){
        Icon icon = VaadinIcon.EDIT.create();
        icon.addClickListener(e -> form.setVisible(true));
        icon.addClassName("icon");
        icon.setTooltipText(tooltip);
        Span iconSpan = new Span(icon);
        iconSpan.getStyle().set("padding-left", "10px");
        return iconSpan;
    }
}

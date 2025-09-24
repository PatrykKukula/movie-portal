package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.CustomDatePicker;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.time.LocalDate;
import java.util.List;



@Route("directors/add")
@PageTitle("Add director")
@RolesAllowed({"ADMIN", "MODERATOR"})
@CssImport("./styles/common-styles.css")
public class AddDirectorView extends Composite<FormLayout> {

    private final DirectorServiceImpl directorService;
    private final BeanValidationBinder<DirectorDto> binder = new BeanValidationBinder<>(DirectorDto.class);
    private Dialog validationDialog;
    private final Notification successNotification = CommonComponents.successNotification("Director added successfully");

    public AddDirectorView(DirectorServiceImpl directorService) {
        this.directorService = directorService;

        CustomDatePicker customDatePicker = new CustomDatePicker();

        DirectorDto dto = new DirectorDto();

        FormLayout layout = getContent();
        layout.addClassName("main-layout");
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        layout.setMaxWidth("25%");

        var firstNameField = FormFields.textField("First name");
        binder.bind(firstNameField, "firstName");

        var lastNameField = FormFields.textField("Last name");
        binder.bind(lastNameField, "lastName");

        var countryField = FormFields.textField("Country");
        binder.bind(countryField, "country");

        var biographyField = FormFields.textAreaField("Biography", 1000);
        binder.bind(biographyField, "biography");

        FormLayout datePickerLayout = customDatePicker.generateDatePickerLayout("Date of birth");

        customDatePicker.setPresentationValue(LocalDate.now());
        binder.bind(customDatePicker, "dateOfBirth");

        Button saveButton = saveButton(binder);
        Button cancelButton = Buttons.cancelButton(DirectorView.class);

        binder.setBean(dto);

        layout.add(firstNameField, lastNameField, countryField, biographyField, datePickerLayout, saveButton, cancelButton);
    }

    private Button saveButton(Binder<DirectorDto> binder){
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                DirectorDto directorDto = binder.getBean();
                Long parameter = directorService.addDirector(directorDto);
                successNotification.open();
                UI.getCurrent().navigate(DirectorDetailsView.class, parameter);
            }
            else {
                List<ValidationResult> validationResults = binder.validate().getValidationErrors();

                validationDialog = CommonComponents.validationErrorsDialog(validationResults);
                validationDialog.open();
            }
        });
        return saveButton;
    }
}

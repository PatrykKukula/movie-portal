package pl.patrykkukula.MovieReviewPortal.View.Director;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.CustomDatePicker;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;


@Slf4j
@Route("directors/edit")
@PageTitle("Edit director")
@RolesAllowed({"ADMIN", "MODERATOR"})
public class DirectorEditView extends Composite<FormLayout> implements HasUrlParameter<Long> {

    private final DirectorServiceImpl directorService;
    private final BeanValidationBinder<DirectorDto> binder = new BeanValidationBinder<>(DirectorDto.class);
    private final Notification successNotification = CommonComponents.successNotification("Director updated successfully");
    private final ConfirmDialog confirmDialog = new ConfirmDialog();
    private Dialog validationDialog;

    public DirectorEditView(DirectorServiceImpl directorService) {
        this.directorService = directorService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long directorId) {
        CustomDatePicker customDatePicker = new CustomDatePicker();

        FormLayout layout = getContent();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        layout.setMaxWidth("25%");
        DirectorDto dto = directorService.fetchDirectorById(directorId);

        var firstNameField = FormFields.textField("First name");
        binder.bind(firstNameField, "firstName");
        var lastNameField = FormFields.textField("Last name");
        binder.bind(lastNameField, "lastName");
        var countryField = FormFields.textField("Country");
        binder.bind(countryField, "country");
        TextArea biographyField = FormFields.textAreaField("Biography", 1000);
        binder.bind(biographyField, "biography");

        FormLayout datePickerLayout = customDatePicker.generateDatePickerLayout("Date of birth");

        customDatePicker.setPresentationValue(dto.getDateOfBirth());
        binder.bind(customDatePicker, "dateOfBirth");

        binder.setBean(dto);
//        binder.setChangeDetectionEnabled(true);
        Button saveButton = saveButton(binder, directorId);
        Button cancelButton = Buttons.cancelButton(DirectorDetailsView.class, directorId);
//        binder.addValueChangeListener(e -> {
//            saveButton.setEnabled(binder.hasChanges());
//        });

        layout.add(firstNameField, lastNameField, countryField, biographyField, datePickerLayout, saveButton, cancelButton);
    }

    private Button saveButton(Binder<DirectorDto> binder, Long directorId) {
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                DirectorDto directorDto = binder.getBean();
                directorService.updateDirectorVaadin(directorId,directorDto);
                successNotification.open();
                UI.getCurrent().navigate(DirectorDetailsView.class, directorId);
            }
            else {
                List<ValidationResult> validationResults = binder.validate().getValidationErrors();

                validationDialog = CommonComponents.validationErrorsDialog(validationResults);
                validationDialog.open();
            }
        });
        return saveButton;
    }
    private void setupConfirmDialog(Long directorId) {
        confirmDialog.setText("You have made changes but did not save them. Do you want to continue?");
        confirmDialog.addConfirmListener(e -> UI.getCurrent().navigate(DirectorDetailsView.class, directorId));
        confirmDialog.addCancelListener(e -> confirmDialog.close());
    }
}

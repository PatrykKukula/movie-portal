package pl.patrykkukula.MovieReviewPortal.View.Actor;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.CustomDatePicker;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.util.List;

@Route("actors/edit")
@PageTitle("Edit actor")
@RolesAllowed({"ADMIN", "MODERATOR"})
public class ActorEditView extends Composite<FormLayout> implements HasUrlParameter<Long> {

    private final ActorServiceImpl actorService;
    private final BeanValidationBinder<ActorDto> binder = new BeanValidationBinder<>(ActorDto.class);
    private final Notification successNotification = CommonComponents.successNotification("Actor updated successfully");
    private Dialog validationDialog;

    public ActorEditView(ActorServiceImpl actorService) {
        this.actorService = actorService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long actorId) {
        CustomDatePicker customDatePicker = new CustomDatePicker();

        FormLayout layout = getContent();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP));
        layout.setMaxWidth("25%");
        ActorDto dto = actorService.fetchActorById(actorId);

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
        Button saveButton = saveButton(binder, actorId);
        Button cancelButton = Buttons.cancelButton(ActorDetailsView.class, actorId);
//        binder.addValueChangeListener(e -> {
//            saveButton.setEnabled(binder.hasChanges());
//        });
        layout.add(firstNameField, lastNameField, countryField, datePickerLayout, biographyField, saveButton, cancelButton);
    }

    private Button saveButton(Binder<ActorDto> binder, Long actorId) {
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                ActorDto actorDto = binder.getBean();
                actorService.updateActorVaadin(actorId, actorDto);
                successNotification.open();
                UI.getCurrent().navigate(ActorDetailsView.class, actorId);
            }
            else {
                List<ValidationResult> validationResults = binder.validate().getValidationErrors();

                validationDialog = new Dialog("Cannot save changes - invalid or empty fields");
                validationDialog = CommonComponents.validationErrorsDialog(validationResults);
                validationDialog.open();
            }
        });
        return saveButton;
    }
}


package pl.patrykkukula.MovieReviewPortal.View.Actor;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomDatePicker;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.time.LocalDate;
import java.util.List;

@Route("actors/add")
@PageTitle("Add actor")
@RolesAllowed("ADMIN")
public class AddActorView extends Composite<FormLayout> {
    private final ActorServiceImpl actorService;
    private final BeanValidationBinder<ActorDto> binder = new BeanValidationBinder<>(ActorDto.class);
    private Dialog validationDialog;

    public AddActorView(ActorServiceImpl actorService) {
        this.actorService = actorService;

        CustomDatePicker customDatePicker = new CustomDatePicker();

        ActorDto dto = new ActorDto();

        FormLayout layout = getContent();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        layout.setMaxWidth("25%");

        var firstNameField = FormFields.textField("First name");
        binder.bind(firstNameField, "firstName");

        var lastNameField = FormFields.textField("Last name");
        binder.bind(lastNameField, "lastName");

        var countryField = FormFields.textField("Country");
        binder.bind(countryField, "country");

        var biographyField = FormFields.textAreaField("Biography");
        binder.bind(biographyField, "biography");

        FormLayout datePickerLayout = customDatePicker.generateDatePickerLayout("Date of birth");

        customDatePicker.setPresentationValue(LocalDate.now());
        binder.bind(customDatePicker, "dateOfBirth");

        Button saveButton = saveButton(binder);
        Button cancelButton = Buttons.cancelButton(ActorView.class);

        binder.setBean(dto);
        binder.validate();

        layout.add(firstNameField, lastNameField, countryField, biographyField, datePickerLayout, saveButton, cancelButton);
    }

    private Button saveButton(Binder<ActorDto> binder){
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                ActorDto actorDto = binder.getBean();
                Long parameter = actorService.addActor(actorDto);
                UI.getCurrent().navigate(ActorDetailsView.class, parameter);
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

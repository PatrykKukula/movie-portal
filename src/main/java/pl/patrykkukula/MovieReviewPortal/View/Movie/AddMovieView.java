package pl.patrykkukula.MovieReviewPortal.View.Movie;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorSummaryDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.*;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.CustomDatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Route("movies/add")
@PageTitle("Add movie")
@CssImport("./styles/common-styles.css")
@RolesAllowed("ADMIN")
public class AddMovieView extends Composite<FormLayout> {

    private final MovieServiceImpl movieServiceImpl;
    private Dialog validationDialog;
    private final DirectorServiceImpl directorService;
    private final ActorServiceImpl actorService;
    private final VerticalLayout pickedActors = new VerticalLayout();
    private final Notification successNotification = CommonComponents.successNotification("Movie added successfully");
    private ComboBox<DirectorSummaryDto> directorField;

    public AddMovieView(MovieServiceImpl movieServiceImpl, DirectorServiceImpl directorService, ActorServiceImpl actorService) {
        this.movieServiceImpl = movieServiceImpl;
        this.directorService = directorService;
        this.actorService = actorService;

        FormLayout layout = getContent();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        layout.setMaxWidth("25%");

        BeanValidationBinder<MovieDto> binder = new BeanValidationBinder<>(MovieDto.class);

        var titleField = FormFields.textField("Title");
        binder.bind(titleField, "title");

        var descriptionField = FormFields.textAreaField("Description", 1000);
        binder.bind(descriptionField, "description");

        CustomDatePicker customDatePicker = new CustomDatePicker();
        FormLayout datePickerLayout = customDatePicker.generateDatePickerLayout("Release date");

        customDatePicker.setPresentationValue(LocalDate.now());
        binder.bind(customDatePicker, "releaseDate");

        ComboBox<MovieCategory> categoryField = FormFields.categoryComboBox( true);
        binder.bind(categoryField, "category");

        directorField = MovieViewCommon.directorComboBox(directorService);
        List<ActorSummaryDto> actors = new ArrayList<>();

        ComboBox<ActorSummaryDto> actorComboBox = MovieViewCommon.actorComboBox(actorService);
        setActorComboBox(actorComboBox, actors);

        Button saveButton = saveButton(binder, actors);
        Button cancelButton = Buttons.cancelButton(MovieView.class);

        MovieDto dto = new MovieDto();
        binder.setBean(dto);
        binder.validate();

        layout.add(titleField, descriptionField, datePickerLayout, categoryField, directorField,
                actorComboBox, pickedActors, saveButton, cancelButton);
    }

    private Button saveButton(Binder<MovieDto> binder, List<ActorSummaryDto> actors) {
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            if (binder.validate().isOk()) {
                MovieDto movieDto = binder.getBean();
                movieDto.setDirectorId(directorField.getValue().getId());
                List<Long> actorIds = actors.stream()
                        .map(ActorSummaryDto::getId)
                        .toList();
                movieDto.setActorIds(actorIds);
                Long parameter = movieServiceImpl.addMovie(movieDto);
                successNotification.open();
                UI.getCurrent().navigate(MovieDetailsView.class, parameter);
            }
            else {
                List<ValidationResult> validationResults = binder.validate().getValidationErrors();
                validationDialog = CommonComponents.validationErrorsDialog(validationResults);
                validationDialog.open();
            }
        });
        return saveButton;
    }

    private void setActorComboBox(ComboBox<ActorSummaryDto> actorComboBox, List<ActorSummaryDto> actors){
        actorComboBox.addValueChangeListener(e -> {
            ActorSummaryDto pickedActor = e.getValue();
            if (!actors.contains(pickedActor) && pickedActor != null) {
                actors.add(pickedActor);

                Div addedActor = MovieViewCommon.addedActor(pickedActor.getFullName());

                Icon closeIcon = MovieViewCommon.closeIcon(pickedActor, addedActor, actors, pickedActors);
                addedActor.add(closeIcon);

                pickedActors.add(addedActor);
            }
            actorComboBox.clear();
        });
    }
}

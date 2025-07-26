package pl.patrykkukula.MovieReviewPortal.View.Common;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationResult;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorView;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommonComponents {

    private CommonComponents() {}

    public static Dialog  validationErrorsDialog(List<ValidationResult> validationResults) {
        Icon closeIcon = VaadinIcon.CLOSE.create();
        closeIcon.addClassName("close-icon");
        closeIcon.getStyle().set("margin-left", "2rem");
//        closeIcon.getStyle().set("color", "red").set("margin-left", "3rem");
//        closeIcon.setSize("1rem");

        Dialog dialog = new Dialog("Cannot save changes - invalid or empty fields");
        dialog.getHeader().addComponentAtIndex(0, closeIcon);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        Div errorMessage = validationErrorsMessage(validationResults);
        errorMessage.getStyle().set("text-align", "center");
        Div closeText = new Div("Press X button or ESC on keyboard to close");
        closeText.getStyle().set("font-size","0.7rem");

        dialogLayout.add(errorMessage, closeText);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.getStyle().set("padding", "0rem 0.8rem");

        dialog.add(dialogLayout);

        closeIcon.addClickListener(e -> dialog.close());

        return dialog;
    }
    public static Notification successNotification(String message){
        Notification notification = new Notification(message);
        notification.setDuration(3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        return notification;
    }
    public static Span labelSpan(String label){
        Span span = new Span(label);
        span.addClassName("bold-component");
        return span;
    }
    public static Icon closeIcon(){
        Icon closeIcon = VaadinIcon.CLOSE.create();
        closeIcon.addClassName("close-icon");
        return closeIcon;
    }
    public static void confirmDelete(Long id, String entity, Consumer<Long> removeEntity, Class<? extends VerticalLayout> clazz) {
        Dialog dialog = new Dialog();
        var text = new Span("Do you want to remove %s? This action cannot be undone.".formatted(entity));

        Button confirmButton = new Button("Confirm delete", e -> {
            removeEntity.accept(id);
            dialog.close();
            UI.getCurrent().navigate(clazz);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = Buttons.cancelButton(dialog);

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.getStyle().set("padding-top", "25px");

        dialog.add(text, buttons);
        dialog.open();
    }
    private static Div validationErrorsMessage(List<ValidationResult> validationResults){
        String joinedErrors = validationResults.stream()
                .map(validationError -> validationError.getErrorMessage())
                .collect(Collectors.joining(System.lineSeparator()));

        Div errorMessage = new Div(joinedErrors);
        errorMessage.getStyle().set("white-space", "pre-line");

        return errorMessage;
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Common;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationResult;
import java.util.List;
import java.util.stream.Collectors;

public class CommonComponents {

    private CommonComponents() {}

    public static Dialog  validationErrorsDialog(List<ValidationResult> validationResults) {
        Icon closeIcon = VaadinIcon.CLOSE.create();
        closeIcon.getStyle().set("color", "red").set("margin-left", "3rem");
        closeIcon.setSize("0.9rem");

        Dialog dialog = new Dialog("Cannot save changes - invalid or empty fields");
        dialog.getHeader().addComponentAtIndex(0, closeIcon);
        dialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        Div errorMessage = validationErrorsMessage(validationResults);
        Div closeText = new Div("Press X button or ESC on keyboard to close");
        closeText.getStyle().set("font-size","0.7rem");

        dialogLayout.add(errorMessage, closeText);
        dialogLayout.setAlignSelf(FlexComponent.Alignment.START, errorMessage);
        dialogLayout.setAlignSelf(FlexComponent.Alignment.CENTER, closeText);
        dialogLayout.getStyle().set("padding", "0.8rem");

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
    private static Div validationErrorsMessage(List<ValidationResult> validationResults){
        String joinedErrors = validationResults.stream()
                .map(validationError -> validationError.getErrorMessage())
                .collect(Collectors.joining(System.lineSeparator()));

        Div errorMessage = new Div(joinedErrors);
        errorMessage.getStyle().set("white-space", "pre-line");

        return errorMessage;
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Common;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import lombok.NoArgsConstructor;

public class Buttons {

    private Buttons (){}

    public static <C extends Component> Button cancelButton(Class<? extends C> navigationTarget) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return cancelButton;
    }
    public static <T, C extends Component & HasUrlParameter<T>> Button cancelButton(Class<? extends C> navigationTarget, T parameter) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget, parameter));
        return cancelButton;
    }
    public static Button cancelButton(Dialog dialog) {
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> dialog.close());
        return cancelButton;
    }
    public static <T, C extends Component & HasUrlParameter<T>> Button editButton(Class<? extends C> navigationTarget, T parameter) {
        Button cancelButton = new Button("Edit");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget, parameter));
        return cancelButton;
    }
    public static <C extends Component> Button backButton(Class<? extends C> navigationTarget, String label) {
        Button cancelButton = new Button(label);
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return cancelButton;
    }

}

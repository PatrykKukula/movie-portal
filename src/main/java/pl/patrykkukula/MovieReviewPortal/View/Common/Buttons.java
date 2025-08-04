package pl.patrykkukula.MovieReviewPortal.View.Common;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.RouteParameters;
import pl.patrykkukula.MovieReviewPortal.View.Topic.AddTopicView;

import java.util.Map;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.RouteParametersConstants.*;

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
    public static <T, C extends Component & HasUrlParameter<T>> Button editButton(Class<? extends C> navigationTarget, String label, T parameter) {
        Button cancelButton = new Button(label);
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget, parameter));
        return cancelButton;
    }
    public static <C extends Component> Button backButton(Class<? extends C> navigationTarget, String label) {
        Button cancelButton = new Button(label);
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        return cancelButton;
    }
    public static <C extends Component> Button addButton(Class<? extends C> navigationTarget, String label) {
        Button addButton = new Button(label);
        addButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget));
        addButton.setPrefixComponent(VaadinIcon.PLUS.create());
        return addButton;
    }
    public static <T, C extends Component & HasUrlParameter<T>> Button addButton(Class<? extends C> navigationTarget, String label, T parameter) {
        Button addButton = new Button(label);
        addButton.addClickListener(e -> UI.getCurrent().navigate(navigationTarget, parameter));
        addButton.setPrefixComponent(VaadinIcon.PLUS.create());
        return addButton;
    }
    public static Button createTopicButton(String entityType, Long entityId){
        Button createTopicButton =  new Button("Create new topic",
                e -> UI.getCurrent().navigate(AddTopicView.class,
                        new RouteParameters(Map.of(ENTITY_TYPE, entityType, ENTITY_ID, entityId.toString()))));
        createTopicButton.getStyle().set("margin-left", "auto").set("margin-top", "20px");

        return createTopicButton;
    }
}

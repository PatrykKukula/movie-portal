package pl.patrykkukula.MovieReviewPortal.View.Account.Components;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class AccountTextFieldComponent extends TextField {
    private String regex;
    private String errorMessage;
    private String label;
    private TextField textField = new TextField();

    public AccountTextFieldComponent(String regex, String errorMessage, String label) {
        this.regex = regex;
        this.errorMessage = errorMessage;
        this.label = label;

        setPattern(regex);
        setLabel(label);
        setValueChangeMode(ValueChangeMode.EAGER);
        setErrorMessage(errorMessage);
    }
}

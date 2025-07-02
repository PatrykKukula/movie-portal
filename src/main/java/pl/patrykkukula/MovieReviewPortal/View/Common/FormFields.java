package pl.patrykkukula.MovieReviewPortal.View.Common;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import pl.patrykkukula.MovieReviewPortal.Constants.MovieCategory;
import pl.patrykkukula.MovieReviewPortal.Model.Director;
import pl.patrykkukula.MovieReviewPortal.Model.Movie;
import pl.patrykkukula.MovieReviewPortal.Repository.DirectorRepository;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;

import java.util.Arrays;
import java.util.List;

public class FormFields {


    private FormFields() {
    }

    public static TextField textField(String label){
        TextField firstNameField = new TextField(label);
        firstNameField.setValueChangeMode(ValueChangeMode.EAGER);
        return firstNameField;
    }
    public static TextArea textAreaField(String label) {
        TextArea textAreaField = new TextArea(label);
        int charLimit = 1000;
        textAreaField.getStyle().set("overflow", "auto");
        textAreaField.setMaxLength(charLimit);
        textAreaField.setValueChangeMode(ValueChangeMode.EAGER);
        textAreaField.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
        });
        textAreaField.setHeight("200px");
        return textAreaField;
    }
    public static TextField searchField(String label, String placeHolder) {
        TextField searchField = new TextField(label);
        searchField.setPlaceholder(placeHolder);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setWidth("25%");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        return searchField;
    }
    public static ComboBox<MovieCategory> categoryComboBox(boolean required) {
        ComboBox<MovieCategory> categoryComboBox = new ComboBox<>("Category");
        List<MovieCategory> categoryItems = Arrays.stream(MovieCategory.values()).sorted().toList();
        categoryComboBox.setItems(categoryItems);
        categoryComboBox.setRequiredIndicatorVisible(required);
        categoryComboBox.setValue(MovieCategory.NONE);
        return categoryComboBox;
    }
    public static PasswordField passwordField() {
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        return passwordField;
    }
}

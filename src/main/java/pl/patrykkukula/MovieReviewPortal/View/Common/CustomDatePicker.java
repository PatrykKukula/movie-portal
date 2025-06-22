package pl.patrykkukula.MovieReviewPortal.View.Common;
import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class CustomDatePicker extends AbstractCompositeField<VerticalLayout, CustomDatePicker, LocalDate> implements HasValidation {

    private final ComboBox<Integer> year = new ComboBox<>("Year");
    private final ComboBox<Month> month = new ComboBox<>("Month");
    private final ComboBox<Integer> day= new ComboBox<>("Day");
    private final NativeLabel errorLabel = new NativeLabel();
    private boolean invalid;

    public CustomDatePicker() {
        super(null);
        setUpYear();
        setUpMonthField();
        day.setEnabled(false);

        errorLabel.getStyle().set("color", "#dc0e0e").set("font-size","13.5px");
        errorLabel.setVisible(false);
    }
    @Override
    public void setPresentationValue(LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            year.setValue(dateOfBirth.getYear());
            month.setValue(dateOfBirth.getMonth());
            day.setValue(dateOfBirth.getDayOfMonth());
        } else {
            year.clear();
            month.clear();
            day.clear();
        }
    }
    public FormLayout generateDatePickerLayout(String label) {
        HorizontalLayout dateFields = new HorizontalLayout(year, month, day);
        VerticalLayout layout = new VerticalLayout(dateFields, errorLabel);

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(layout, label);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px",1, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        return formLayout;
    }

    private void setUpYear(){
        List<Integer> years = IntStream.range(1900, Year.now().getValue()+1).boxed()
                .toList();
        year.setItems(years);
        year.setWidth("25%");
        year.addValueChangeListener(e -> updateMonthField());
        year.setRequiredIndicatorVisible(true);
    }
    private void setUpMonthField(){
        month.setItems(Month.values());
        month.setItemLabelGenerator(m -> m.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        month.addValueChangeListener(e -> updateDayField());
        month.setWidth("30%");
        month.setEnabled(false);
        month.setRequiredIndicatorVisible(true);
    }
    private void setUpDayField(LocalDate date){
        int length = date.lengthOfMonth();
        List<Integer> daysOfMonth = IntStream.range(1, length+1).boxed().toList();
        day.setItems(daysOfMonth);
        day.setWidth("16%");
        day.setRequiredIndicatorVisible(true);
    }
    private void updateMonthField(){
        month.setEnabled(year.getValue() != null);
    }
    private void updateDayField(){
        if (year.getValue() != null && month.getValue() != null){
            LocalDate date = LocalDate.of(year.getValue(), month.getValue(),1);
            setUpDayField(date);
            day.setEnabled(true);
            day.addValueChangeListener(e -> updateValue());
        }
        else {
            day.setValue(null);
            day.setEnabled(false);
        }
    }
    private void updateValue() {
        if (year.getValue() != null && month.getValue() != null && day.getValue() != null) {
            LocalDate date = LocalDate.of(year.getValue(), month.getValue(), day.getValue());
            setModelValue(date, true);
        }
        else {
            setModelValue(null, true);
        }
    }
    @Override
    public void setErrorMessage(String errorMessage) {
        errorLabel.setText(errorMessage);
    }
    @Override
    public String getErrorMessage() {
        return errorLabel.getText();
    }
    @Override
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
        errorLabel.setVisible(invalid);
    }
    @Override
    public boolean isInvalid() {
        return invalid;
    }
}

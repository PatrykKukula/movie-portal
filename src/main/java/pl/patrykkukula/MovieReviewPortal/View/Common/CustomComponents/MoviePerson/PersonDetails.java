package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;

public class PersonDetails extends VerticalLayout {
    public PersonDetails( String countrySup, String dateSup) {
        setPadding(false);

        Span country = createDetail("Country: ", countrySup);
        Span dateOfBirth = createDetail("Date of birth: ", dateSup);

        add(country, dateOfBirth);
    }
    private Span createDetail(String label, String value){
        Span detail = new Span(CommonComponents.labelSpan(label));
        detail.add(value);
        return detail;
    }
}

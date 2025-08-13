package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;

public class BiographyDiv extends Div {
    public BiographyDiv(String biography) {
        Span biographyLabel = CommonComponents.labelSpan("Biography: ");
        add(biographyLabel);
        add(biography);
    }
}

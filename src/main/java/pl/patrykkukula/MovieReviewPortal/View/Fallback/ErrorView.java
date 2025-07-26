package pl.patrykkukula.MovieReviewPortal.View.Fallback;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import pl.patrykkukula.MovieReviewPortal.View.MainLayout;

@Tag("div")
@Route(value = "error", layout = MainLayout.class)
@PermitAll
@CssImport("./styles/error-styles.css")
@PageTitle("Error")
public class ErrorView extends Div implements HasErrorParameter<RuntimeException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<RuntimeException> ex) {
        String message = ex.getCustomMessage() != null ?
                ex.getCustomMessage() :
                "Something went wrong...";

        addClassName("error-div");
        setText("An error has occurred: " + message);
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}

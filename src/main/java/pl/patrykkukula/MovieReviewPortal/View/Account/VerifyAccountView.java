package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.AuthServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.MainView;

import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.View.Account.AccountViewConstants.*;

@Route("verify")
@PageTitle("Verify account")
@AnonymousAllowed
public class VerifyAccountView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final AuthServiceImpl authService;
    private final Dialog dialog = new Dialog();

    public VerifyAccountView(AuthServiceImpl authService) {
        this.authService = authService;
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try{
            String token = event.getLocation().getQueryParameters()
                    .getParameters()
                    .getOrDefault("token", List.of(""))
                    .getFirst();
            authService.verifyAccount(token);
            Div verified = new Div(ACCOUNT_VERIFIED_SUCCESS);
            dialog.add(verified);
            dialog.addOpenedChangeListener( e -> {
                if (!e.isOpened()){
                    UI.getCurrent().navigate(MainView.class);
                }
            }
            );
            dialog.open();

        } catch(IllegalStateException | IllegalArgumentException ex){
            Div message = new Div(ex.getMessage());
            dialog.add(message);
            dialog.open();
        }
    }
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        dialog.close();
    }
}

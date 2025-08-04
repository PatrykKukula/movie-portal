package pl.patrykkukula.MovieReviewPortal.View.Account.Components;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

    public class EditFieldForm extends FormLayout {
        /*
            Form layout to edit account related data
         */
        public EditFieldForm (UserEntity user, UserEntityRepository userRepository, UserDetailsServiceImpl userDetailsService,
                            String regex, String errorMessage, String fieldLabel, String buttonLabel,
                            Span dialogText, String dialogTextValue, Dialog dialog, Span fieldValue,
                            Supplier<String> getField, BiPredicate<UserEntity, String> serviceMethod, boolean reAuthenticate) {
            setVisible(false);
            setResponsiveSteps(new ResponsiveStep("0px", 1));
            addClassName("form-layout");
            TextField field = new AccountTextFieldComponent(regex, errorMessage, fieldLabel);
            if (getField.get() != null) {
                field.setValue(getField.get());
            }
            else field.setValue("");

            Button change = new Button(buttonLabel);
            change.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            change.addClickListener(e -> {
                boolean isChanged = serviceMethod.test(user, field.getValue());
                if (isChanged) {
                    setVisible(false);
                    dialogText.removeAll();
                    dialogText.setText(dialogTextValue);
                    dialog.open();

                    Optional<UserEntity> updatedUserOpt = userRepository.findById(user.getUserId());
                    if (updatedUserOpt.isPresent()) {
                        String updatedField = getField.get();
                        fieldValue.removeAll();
                        fieldValue.setText(updatedField);
                        if (reAuthenticate) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(updatedField);
                            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            });
            Button cancel = new Button("Cancel");
            cancel.addClickListener(e -> setVisible(false));

            add(field, change, cancel);
        }
    }

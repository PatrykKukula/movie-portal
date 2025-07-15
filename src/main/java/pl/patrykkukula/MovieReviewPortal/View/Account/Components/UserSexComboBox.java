package pl.patrykkukula.MovieReviewPortal.View.Account.Components;

import com.vaadin.flow.component.combobox.ComboBox;
import pl.patrykkukula.MovieReviewPortal.Constants.UserSex;

public class UserSexComboBox extends ComboBox<UserSex> {

    public UserSexComboBox(){
        setItems(UserSex.values());
        setLabel("Sex");
    }
}

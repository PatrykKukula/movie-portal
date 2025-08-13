package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PagedList;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PageButtons;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class FilterButton extends Button {
    public <T> FilterButton(TextField searchField, VerticalLayout layout, ComboBox<String> sortingBox, PagedList<T> pagedList,
                            BiFunction<String, String, List<T>> fetchSupplier, PageButtons pageButtons, Consumer<VerticalLayout> renderView){
            setText("Search");
            getStyle().set("margin-top", "37px");
            addClickListener(e -> {
                String name = searchField.getValue();
                String sort = sortingBox.getValue();

                List<T> list = fetchSupplier.apply(name,sort);
                pagedList.setList(list);
                pageButtons.setUp(layout, pagedList, renderView);
                renderView.accept(layout);
            });
    }
}

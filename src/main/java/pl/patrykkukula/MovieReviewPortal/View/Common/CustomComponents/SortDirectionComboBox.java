package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.combobox.ComboBox;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.*;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.SORT_ASC;

public class SortDirectionComboBox extends ComboBox<String> {
    public SortDirectionComboBox(TopicSectionLayout commentSectionLayout){
        setLabel("Sort by");
        setItems(LATEST, OLDEST);
        setValue(LATEST);
        getStyle().set("padding-left", "10px");

       addValueChangeListener(e -> {
            if (e.getValue().equals(OLDEST)) {
                commentSectionLayout.updateCommentSectionLayout(SORT_DESC);
            }
            else commentSectionLayout.updateCommentSectionLayout(SORT_ASC);
        });
    }
}

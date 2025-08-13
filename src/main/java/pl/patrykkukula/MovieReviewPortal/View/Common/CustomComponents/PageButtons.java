package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class PageButtons extends Div {
    private final Button nextButton = new Button(VaadinIcon.ARROW_RIGHT.create());
    private final Button previousButton = new Button(VaadinIcon.ARROW_LEFT.create());
    private final Span page = new Span();
    private int currentPage;
    private int totalPages;
    private final Div div;
    private VerticalLayout layout;
    private PagedList<?> pagedList;
    private Consumer<VerticalLayout> renderView;

    public PageButtons(int currentPage, int totalPages){
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        setCurrentPageNumber(currentPage, totalPages);
        div = buttonsLayout();
        setVisible();
        configureListeners();
        add(div);
    }
    public void setCurrentPageNumber(int currentPage, int totalPages){
        this.currentPage = currentPage;
        page.setText(currentPage + 1 + " / " + totalPages);
    }
    public <T> void configurePageButtons(UserDetails user, Long id, PagedList<T> pagedList, BiConsumer<UserDetails, Long> renderView){
        nextButton.setEnabled(currentPage < totalPages - 1);
        previousButton.setEnabled(false);
        nextButton.addClickListener(e -> {
            currentPage++;
            boolean isValid = pagedList.isValidPage(currentPage);
            nextButton.setEnabled(currentPage < totalPages - 1);
            previousButton.setEnabled(currentPage != 0);
            if (isValid) {
                renderView.accept(user, id);
                page.setText(currentPage + 1 + " / " + totalPages);
            }
        });
        previousButton.addClickListener(e -> {
            currentPage--;
            boolean isValid = pagedList.isValidPage(currentPage);
            previousButton.setEnabled(currentPage != 0);
            nextButton.setEnabled(currentPage < totalPages - 1);
            if (isValid) {
                renderView.accept(user, id);
                page.setText(currentPage + 1 + " / " + totalPages);
            }
        });
    }
    public <T> void setUp(VerticalLayout layout, PagedList<T> pagedList, Consumer<VerticalLayout> renderView){
        this.layout = layout;
        this.pagedList = pagedList;
        this.renderView = renderView;
        this.currentPage = 0;
        this.totalPages = pagedList.getTotalPages();
        setCurrentPageNumber(currentPage, totalPages);
        updateButtons();
        setVisible();
    }
    private void configureListeners(){
        nextButton.addClickListener(e -> {
            currentPage++;
            boolean isValid = pagedList.isValidPage(currentPage);
            nextButton.setEnabled(currentPage < totalPages - 1);
            previousButton.setEnabled(currentPage != 0);
            if (isValid) {
                renderView.accept(layout);
                page.setText(currentPage + 1 + " / " + totalPages);
            }
        });
        previousButton.addClickListener(e -> {
            currentPage--;
            boolean isValid = pagedList.isValidPage(currentPage);
            previousButton.setEnabled(currentPage != 0);
            nextButton.setEnabled(currentPage < totalPages - 1);
            if (isValid) {
                renderView.accept(layout);
                page.setText(currentPage + 1 + " / " + totalPages);
            }
        });
    }
    private void updateButtons() {
        nextButton.setEnabled(currentPage < totalPages - 1);
        previousButton.setEnabled(currentPage != 0);
    }
    private Div buttonsLayout(){
        return new Div(previousButton, page, nextButton);
    }
    private void setVisible(){
        div.setVisible(totalPages>0);
    }
    public int getCurrentPage(){
        return this.currentPage;
    }
}

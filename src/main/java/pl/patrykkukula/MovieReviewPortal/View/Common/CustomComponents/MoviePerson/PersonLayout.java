package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.MoviePerson;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasUrlParameter;
import org.checkerframework.checker.units.qual.C;
import pl.patrykkukula.MovieReviewPortal.Dto.ViewableEntity;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.IImageService;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PagedList;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.*;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.reflections.Reflections.log;
import static pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents.*;

/*
    Layout for view with paged person entities
 */
public class PersonLayout<T extends ViewableEntity> extends VerticalLayout {
    private final PagedList<T> pagedList;
    private final PageButtons pageButtons;
    private final VerticalLayout entityLayout;
    private final Function<T, HorizontalLayout> layoutRenderer;
    private final BiFunction<String, String, List<T>> fetchFunction;
    private final IImageService imageService;
    private final String imagePath;
    private final String placeholderPath;
    private final int PAGE_SIZE = 2;
    private static final String WIDTH = "100px";
    private static final String HEIGHT = "140px";

    public PersonLayout(String titleText, String addLabel, Class<? extends Component> addTarget,
                        IImageService imageService, String imagePath, String placeholderPath,
                        BiFunction<String, String, List<T>> fetchFunction,
                        Function<T, HorizontalLayout> layoutRenderer,
                        UserDetailsServiceImpl userDetailsService) {
        this.fetchFunction = fetchFunction;
        this.imageService = imageService;
        this.imagePath = imagePath;
        this.placeholderPath = placeholderPath;
        this.layoutRenderer = layoutRenderer;

        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);

        H1 title = new H1(titleText);
        title.addClassName("view-title");

        Button addEntityButton = Buttons.addButton(addTarget, addLabel);

        List<T> list = fetchFunction.apply("", "ASC");
        pagedList = new PagedList<>(list, PAGE_SIZE);

        entityLayout = new VerticalLayout();
        entityLayout.addClassName("details-layout");

        pageButtons = new PageButtons(0, pagedList.getTotalPages());
        Consumer<VerticalLayout> consumer = renderPage();
        consumer.accept(entityLayout);
        pageButtons.setUp(entityLayout, pagedList, consumer);

        ComboBox<String>sortingBox = sortingComboBox();
        TextField searchField = FormFields.searchField("Search by name or last name", "Enter name or last name..");
        Button filterButton = new FilterButton(searchField, entityLayout, sortingBox, pagedList, fetchFunction, pageButtons, consumer);
        HorizontalLayout searchFieldLayout = searchFieldLayout(searchField, sortingBox, filterButton);

        add(title, searchFieldLayout, entityLayout, pageButtons);

        if (userDetailsService.isAdmin()) {
            addComponentAtIndex(1, addEntityButton);
        }
    }

    private Consumer<VerticalLayout> renderPage() {
        return layout -> {
            int currentPage = pageButtons.getCurrentPage();
            List<T> page = pagedList.getPage(currentPage);
            layout.removeAll();
            page.forEach(entity -> layout.add(layoutRenderer.apply(entity)));
        };
    }
    public static <T extends ViewableEntity, C extends Component & HasUrlParameter<Long>> HorizontalLayout createLayout(IImageService imageService, T entity, Class<? extends C> detailView,
    String dir, String dirPh) {
        HorizontalLayout layout = new HorizontalLayout();
        Div rightSide = new Div();

        try {
            Poster poster = new Poster(imageService, entity.getId(),dir, dirPh, WIDTH, HEIGHT);
            rightSide.add(poster);
        } catch (IOException ex) {
            log.warn("Error loading image: {}", ex.getMessage());
        }

        VerticalLayout details = new VerticalLayout();
        details.add(new Div(new Span(labelSpan("Name: ")), new Span(entity.getFirstName() + " " + entity.getLastName())));
        details.add(new Div(new Span(labelSpan("Country: ")), new Span(entity.getCountry())));
        details.add(new Div(new Span(labelSpan("Birth date: ")), new Span(entity.getDateOfBirth().toString())));
        details.add(new Div(new Span(labelSpan("Rate: ")), new Span(" %s with %s votes".formatted(
                String.format("%.2f", entity.getAverageRate()), entity.getRateNumber()
        ))));
        details.getStyle().set("padding", "0 0 35px 15px");
        details.setWidthFull();

        layout.add(rightSide, details);
        layout.setWidthFull();
        layout.getStyle().set("border-bottom", "1px solid lightgrey").set("cursor", "pointer");

        layout.addClickListener(e -> UI.getCurrent().navigate(detailView, entity.getId()));
        return layout;
    }
    private HorizontalLayout searchFieldLayout(TextField searchField, ComboBox<String> sortingBox, Button filterButton) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.add(searchField,sortingBox, filterButton);
        layout.addClassName("details-layout");
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
        setHeight("100px");
        return layout;
    }
    private ComboBox<String> sortingComboBox(){
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLabel("Order by name");
        comboBox.setItems(List.of("ASC", "DESC"));
        comboBox.setItemLabelGenerator(e -> {
            if (e.equals("ASC")) return "Ascending";
            else return "Descending";
        });
        comboBox.setValue("ASC");
        return comboBox;
    }
}

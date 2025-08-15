package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.data.domain.Page;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image.Poster;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Movie.MovieDetailsView;

import java.io.IOException;

import static org.reflections.Reflections.log;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PosterConstants.*;

public class SingleEntityLayoutWithPoster<T extends EntityWithRate> extends HorizontalLayout {
    private final ImageServiceImpl imageService;
    private final VerticalLayout layout = new VerticalLayout();
    private Page<T> entityPage;
    private TriFunction<Long, Integer, Integer, Page<T>> fetchPage;
    private final Button prev = new Button(VaadinIcon.ARROW_LEFT.create());
    private final Button next = new Button(VaadinIcon.ARROW_RIGHT.create());
    private String entityType;
    private Long userId;
    private final Span page = new Span();
    private Integer pageNo = 0;
    private final Integer PAGE_SIZE = 2;
    private final String WIDTH = "140px";
    private final String HEIGHT = "180px";

    public SingleEntityLayoutWithPoster(TriFunction<Long, Integer, Integer, Page<T>> fetchPage, Long userId, ImageServiceImpl imageService) {
        this.imageService = imageService;
        this.fetchPage = fetchPage;
        this.userId = userId;
        renderView();
        setUpButtons();
        addClassName("details-layout");
        HorizontalLayout pagingLayout = pagingLayout();
        layout.add();
        VerticalLayout mainLayout = new VerticalLayout(layout, pagingLayout);
        if (entityPage.getTotalPages() == 0){
            mainLayout.add(noRatedDiv());
        }

        add(mainLayout);
    }
    public SingleEntityLayoutWithPoster(ImageServiceImpl imageService){
        this.imageService = imageService;
    }
    public void addEntityToLayout(EntityWithRate entity, HorizontalLayout topRatedLayout){
        VerticalLayout layout = renderSingleEntityLayout(entity);
        topRatedLayout.add(layout);
    }
    private void renderView(){
        entityPage = fetchPage.apply(userId, pageNo, PAGE_SIZE);

        layout.removeAll();

        HorizontalLayout singleLine = new HorizontalLayout();

        for(int i = 0; i < entityPage.getNumberOfElements(); i++){
            VerticalLayout singleEntityLayout = this.renderSingleEntityLayout(entityPage.getContent().get(i));
            singleLine.add(singleEntityLayout);
            layout.add(singleLine);
            if (i != 0 && i % 4 == 0){
                singleLine = new HorizontalLayout();
            }
        }
    }
    private VerticalLayout renderSingleEntityLayout(EntityWithRate entity){
        VerticalLayout layout = new VerticalLayout();
        String dir = "";
        String dirPh = "";

        Integer rate = entity.getUserRate();
        entityType = entity.getType();
        switch (entityType){
            case "Movie" -> {
                dir = MOV_DIR;
                dirPh = MOV_DIR_PH;
                layout.addClickListener(e -> UI.getCurrent().navigate(MovieDetailsView.class, entity.getId()));
            }
            case "Actor" -> {
                dir = ACT_DIR;
                dirPh = ACT_DIR_PH;
                layout.addClickListener(e -> UI.getCurrent().navigate(ActorDetailsView.class, entity.getId()));
            }
            case "Director" -> {
                dir = DIR_DIR;
                dirPh = DIR_DIR_PH;
                layout.addClickListener(e -> UI.getCurrent().navigate(DirectorDetailsView.class, entity.getId()));
            }
        }
        try {
            Poster poster = new Poster(imageService, entity.getId(), dir, dirPh, WIDTH, HEIGHT);
            Div posterWrapper = new Div();
            posterWrapper.getStyle()
                    .set("position", "relative")
                    .set("display", "inline-block");
            posterWrapper.add(poster);

            Div ratingOverlay = new Div(starIcon(), new Div(String.valueOf(rate != null ? rate : entity.getAverageRate())));
            ratingOverlay.getStyle()
                    .set("position", "absolute")
                    .set("top", "5px")
                    .set("left", "5px")
                    .set("background", "rgba(0,0,0,0.6)")
                    .set("color", "white")
                    .set("padding", "2px 5px")
                    .set("border-radius", "5px")
                    .set("font-size", "12px");
            posterWrapper.add(ratingOverlay);

            layout.add(posterWrapper);
        }
        catch (IOException ex){
            log.warn("Error loading poster:{} ", ex.getMessage(), ex);
        }
        layout.add(entity.getText());
        layout.getStyle().set("cursor", "pointer");
        return layout;
    }
    private HorizontalLayout pagingLayout(){
        HorizontalLayout pagingLayout = new HorizontalLayout();
        pagingLayout.getStyle().set("align-self", "center");
        setPageText();
        page.getStyle().set("margin-top", "5px");

        pagingLayout.add(prev, page, next);
        pagingLayout.setVisible(entityPage.getTotalPages() > 0);
        return pagingLayout;
    }
    private void setUpButtons(){
        next.setEnabled(pageNo + 1 < entityPage.getTotalPages());
        prev.setEnabled(pageNo > 0);
        next.addClickListener(e ->{
            pageNo++;
            next.setEnabled(pageNo + 1 < entityPage.getTotalPages());
            prev.setEnabled(pageNo > 0);
            setPageText();
            renderView();
        });
        prev.addClickListener(e -> {
            pageNo--;
            next.setEnabled(pageNo < entityPage.getTotalPages());
            prev.setEnabled(pageNo > 0);
            setPageText();
            renderView();
        });
    }
    public Div setHeader(Long userId, UserDetailsServiceImpl userDetailsService){
        UserEntity user = userDetailsService.loadUserEntityById(userId);

        Anchor link = new Anchor("user/%s".formatted(userId), user.getFirstName() + " " + user.getLastName());
        Div div = new Div((new Span("Movies rated by ")), link, new Span(" (%s)".formatted(user.getUsername())));
        div.addClassName("detail");
        div.getStyle().set("align-self", "center");
        return div;
    }
    private Icon starIcon(){
        Icon star = VaadinIcon.STAR.create();
        star.getStyle().set("font-size", "12px").set("margin-right", "14px").set("color", "blue");
        return star;
    }
    private void setPageText(){
        page.setText(pageNo + 1 + "/" + entityPage.getTotalPages());
    }
    private Div noRatedDiv(){
        Div div = new Div("No rates added");
        div.addClassName("detail");
        div.getStyle().set("align-self", "center");

        return div;
    }
}

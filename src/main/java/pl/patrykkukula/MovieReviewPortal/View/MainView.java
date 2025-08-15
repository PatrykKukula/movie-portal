package pl.patrykkukula.MovieReviewPortal.View;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.EntityWithRate;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.MainViewTopicDto;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.*;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorView;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.SingleEntityLayoutWithPoster;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorView;
import pl.patrykkukula.MovieReviewPortal.View.Movie.MovieView;
import pl.patrykkukula.MovieReviewPortal.View.Topic.TopicDetailsView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

@Route
@PageTitle("Movie Portal")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class MainView extends VerticalLayout {
    private final TopicServiceImpl topicService;
    private final MovieServiceImpl movieService;
    private final ActorServiceImpl actorService;
    private final DirectorServiceImpl directorService;
    private final ImageServiceImpl imageService;
    private SingleEntityLayoutWithPoster<?> singleEntityLayout;
    private final VerticalLayout latestTopics = new VerticalLayout();


    public MainView(TopicServiceImpl topicService, MovieServiceImpl movieService, ActorServiceImpl actorService, DirectorServiceImpl directorService, ImageServiceImpl imageService) {
        this.topicService = topicService;
        this.movieService = movieService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.imageService = imageService;
        addClassName("main-layout");
        getStyle().set("align-items", "center");

        singleEntityLayout = new SingleEntityLayoutWithPoster<>(imageService);

        latestTopics.getStyle().set("align-items", "center");
        Div welcomeDiv = welcomeText();

        List<MainViewTopicDto> topics = topicService.fetchLatestTopics();
        renderLatestTopics(topics);

        VerticalLayout topRatedMoviesLayout = topRatedLayout(movieService::fetchTopRatedMovies, "Movies");
        VerticalLayout topRatedActorsLayout = topRatedLayout(actorService::fetchTopRatedActors, "Actors");
        VerticalLayout topRatedDirectorsLayout = topRatedLayout(directorService::fetchTopRatedDirectors, "Directors");

        add(welcomeDiv, latestTopics, topRatedMoviesLayout, topRatedActorsLayout, topRatedDirectorsLayout);
    }
    private void renderLatestTopics(List<MainViewTopicDto> topics){
        latestTopics.removeAll();
        Div latest = new Div("Latest topics");
        latest.addClassName("detail");
        latestTopics.add(latest);
        for (MainViewTopicDto topic : topics){
            latestTopics.add(singleTopicLayout(topic));
        }
    }
    private VerticalLayout singleTopicLayout(MainViewTopicDto topic){
        VerticalLayout layout = new VerticalLayout();
        Span title = new Span(topic.getTitle());
        title.getStyle().set("cursor", "pointer");

        Anchor createdBy = new Anchor("user/%s".formatted(topic.getUserId()), topic.getCreatedBy());
        Span created = new Span(new Span("Posted by: "), createdBy, new Span(" Posted at: %s"
                .formatted(topic.getCreatedAt() != null ? topic.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) : "")));
        created.getStyle().set("font-size", "10.5px");

        String href = "";
        String entityType = topic.getEntityType();
        switch (entityType){
            case "actor" -> href = "actors/%s".formatted(topic.getEntityId());
            case "director" -> href = "directors/%s".formatted(topic.getEntityId());
            case "movie" -> href = "movies/%s".formatted(topic.getEntityId());
        }

        Anchor entity = new Anchor(href, topic.getEntityName());
        Span topicOn = new Span(new Span("Topic on: "), entity);
        topicOn.getStyle().set("font-size", "10.5px");

        HorizontalLayout leftSide = new HorizontalLayout(topicOn, created);
        leftSide.setWidthFull();
        leftSide.setJustifyContentMode(JustifyContentMode.BETWEEN);

        layout.add(title, leftSide);
        layout.setSpacing("var(--lumo-space-s)");
        layout.getStyle().set("border-bottom", "1px solid grey").set("width", "700px").set("padding", "5px 0");

        title.addClickListener(e -> UI.getCurrent().navigate(TopicDetailsView.class, topic.getId()));

        return layout;
    }
    private VerticalLayout topRatedLayout(Supplier<List<EntityWithRate>> fetchList, String type){
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("details-layout");

        Div header = new Div("Top rated %s".formatted(type));
        header.addClassName("detail");
        header.getStyle().set("align-self", "center");

        List<EntityWithRate> list = fetchList.get();

        HorizontalLayout topRatedLayout = new HorizontalLayout();
        topRatedLayout.getStyle().set("margin", "auto").set("margin-bottom", "10px");
        renderTopRatedLayout(list, topRatedLayout);

        Button button = browseButton(type);

        layout.add(header, topRatedLayout, button);
        return layout;
    }
    private void renderTopRatedLayout(List<? extends EntityWithRate> list, HorizontalLayout topRatedLayout){
        topRatedLayout.removeAll();
        for(EntityWithRate entity : list){
            singleEntityLayout.addEntityToLayout(
                    entity,
                    topRatedLayout
            );
        }
    }
    private Button browseButton(String type){
        Button button = new Button("Browse all");

        switch (type){
            case "Actors" -> button.addClickListener(e -> UI.getCurrent().navigate(ActorView.class));
            case "Directors" -> button.addClickListener(e -> UI.getCurrent().navigate(DirectorView.class));
            case "Movies" -> button.addClickListener(e -> UI.getCurrent().navigate(MovieView.class));
        }
        button.getStyle().set("align-self", "center").set("color", "dark-blue");

        return button;
    }

    private Div welcomeText(){
        Div div = new Div("Welcome to Movie Portal - a place for the movie lovers community");
        div.getStyle().set("font-weight", "bold").set("font-size", "28px").set("padding-bottom", "15px").set("border-bottom", "1.5px solid blue");
        return div;
    }
}

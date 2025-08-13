package pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Topic.TopicDetailsView;

import java.time.format.DateTimeFormatter;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.PageableConstants.*;

public class TopicSectionLayout extends VerticalLayout {
    private final TopicServiceImpl topicService;
    private final UserDetailsServiceImpl userDetailsService;
    private Page<TopicDtoBasic> allTopics;
    private final Long entityId;
    private final int pageSize;
    private final String sort;
    private final String entityType;
    private final Button nextButton = new Button(VaadinIcon.ARROW_RIGHT.create());
    private final Button previousButton = new Button(VaadinIcon.ARROW_LEFT.create());
    private final Span page = new Span();
    private final int initialPage;
    VerticalLayout topicLayout = new VerticalLayout();

    public TopicSectionLayout(TopicServiceImpl topicService, UserDetailsServiceImpl userDetailsService, Long entityId,
                              int initialPage, int pageSize, String sort, String entityType
    ) {
        this.topicService = topicService;
        this.userDetailsService = userDetailsService;
        this.entityId = entityId;
        this.sort = sort;
        this.entityType = entityType;
        this.pageSize = pageSize;
        this.initialPage = initialPage;

        addClassName("details-layout");
        getStyle().set("padding-bottom", "20px");
        setAlignItems(Alignment.CENTER);

        allTopics = topicService.findAllTopics(initialPage, pageSize, sort, entityType, entityId);

        Button createTopicButton = Buttons.createTopicButton(entityType, entityId);
        Div pageButtons = new Div();
        pageButtons.add(previousButton, page, nextButton);
        pageButtons.setVisible(areTopicsAvailable());
        H2 header = new H2("Topics");
        ComboBox<String> sortComboBox = sortDirectionComboBox(this);
        HorizontalLayout firstLine = new HorizontalLayout(header, sortComboBox);
        firstLine.getStyle().set("padding-left", "15px").set("width", "100%").set("align-items", "center").set("justify-content", "start");

        UserDetails user = userDetailsService.getAuthenticatedUser();
        if (user != null) {
            firstLine.addComponentAtIndex(2, createTopicButton);
        }
        updatePagingButtons(allTopics.hasNext(), allTopics.hasPrevious(), allTopics.getNumber(), allTopics.getTotalPages());
        addPageButtonsListeners(entityId);
        updateTopicsLayout(allTopics);
        add(firstLine, topicLayout, pageButtons);
    }
    private void updateTopicsLayout(Page<TopicDtoBasic> allTopics){
        topicLayout.removeAll();
        if (areTopicsAvailable()){
        allTopics.map(topic -> {
            VerticalLayout singleTopicLayout = getSingleTopicLayout(topic);

            topicLayout.add(singleTopicLayout);
            return singleTopicLayout;
        });
        }
        else topicLayout.add(new Div("No topics available"));
    }
    private void updatePagingButtons(boolean hasNext, boolean hasPrevious, int currentPage, int lastPage){
        nextButton.setEnabled(hasNext);
        previousButton.setEnabled(hasPrevious);
        page.setText(((currentPage + 1) + " / " + lastPage));
    }
    private void addPageButtonsListeners(Long entityId){
        nextButton.addClickListener(e -> {
            if (allTopics.hasNext()) {
                Pageable pageable = allTopics.nextPageable();
                allTopics = topicService.findAllTopics(pageable.getPageNumber(), pageSize, sort, entityType, entityId);
                updatePagingButtons(allTopics.hasNext(), allTopics.hasPrevious(), allTopics.getNumber(), allTopics.getTotalPages());
                updateTopicsLayout(allTopics);
            }
        });
        previousButton.addClickListener(e -> {
            if (allTopics.hasPrevious()) {
                Pageable pageable = allTopics.previousPageable();
                allTopics = topicService.findAllTopics(pageable.getPageNumber(), pageSize, sort, entityType, entityId);
                updatePagingButtons(allTopics.hasNext(), allTopics.hasPrevious(), allTopics.getNumber(), allTopics.getTotalPages());
                updateTopicsLayout(allTopics);
            }
        });
    }
    private VerticalLayout getSingleTopicLayout(TopicDtoBasic topic){
        VerticalLayout singleTopicLayout = new VerticalLayout();
        singleTopicLayout.getStyle().set("width", "100%").set("border", "1px solid gray").set("border-radius", "10px").set("cursor", "pointer");

        Div topicTitle = new Div(topic.getTitle());
        topicTitle.getStyle().set("border-bottom", "0.5px solid black").set("font-weight", "bold").setWidth("100%").set("padding-bottom", "10px");

        String createdAtFormatted = topic.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Span createdAt = new Span(new Span("Post date: "), new Span(createdAtFormatted));
        String username = userDetailsService.getUsername(topic.getCreatedBy());
        Span createdBy = new Span(new Span("Created by: "), new Span(username));
        Span postCount = new Span(new Span("Comments: "), new Span(String.valueOf(topic.getPostCount())));

        createdAt.addClassName("post-detail");
        createdBy.addClassName("post-detail");
        createdBy.getStyle().set("padding-left", "10px");
        postCount.addClassName("post-detail");
        postCount.getStyle().set("margin-left", "auto");

        HorizontalLayout topicDetails = new HorizontalLayout(createdAt, createdBy, postCount);
        topicDetails.getStyle().set("width", "100%");
        singleTopicLayout.add(topicTitle, topicDetails);
        addSingleTopicListener(topic.getId(), singleTopicLayout);
        return singleTopicLayout;
    }
    private void addSingleTopicListener(Long topicId, VerticalLayout singleTopicLayout){
        singleTopicLayout.addClickListener(e -> UI.getCurrent().navigate(TopicDetailsView.class, topicId));
    }
    public boolean areTopicsAvailable(){
        return allTopics.hasContent();
    }
    public void updateCommentSectionLayout(String sort){
        allTopics = topicService.findAllTopics(initialPage, pageSize, sort, entityType, entityId);
        topicLayout.removeAll();
        if (areTopicsAvailable()){
            allTopics.map(topic -> {
                VerticalLayout singleTopicLayout = getSingleTopicLayout(topic);

                topicLayout.add(singleTopicLayout);
                return singleTopicLayout;
            });
        }
       else topicLayout.add(new Div("No topics available"));
    }
    private ComboBox<String>  sortDirectionComboBox(TopicSectionLayout commentSectionLayout){
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLabel("Sort by");
        comboBox.setItems(LATEST, OLDEST);
        comboBox.setValue(LATEST);
        comboBox.getStyle().set("padding-left", "10px");

        comboBox.addValueChangeListener(e -> {
            if (e.getValue().equals(OLDEST)) {
                commentSectionLayout.updateCommentSectionLayout(SORT_DESC);
            }
            else commentSectionLayout.updateCommentSectionLayout(SORT_ASC);
        });
        return comboBox;
    }
}

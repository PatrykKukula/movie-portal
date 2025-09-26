package pl.patrykkukula.MovieReviewPortal.View.Topic;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Dto.Actor.ActorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Director.DirectorDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Movie.MovieDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoWithCommentDto;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ActorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.DirectorServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.MovieServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Actor.ActorDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Common.Buttons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CommonComponents;
import pl.patrykkukula.MovieReviewPortal.View.Common.FormFields;
import pl.patrykkukula.MovieReviewPortal.View.Director.DirectorDetailsView;
import pl.patrykkukula.MovieReviewPortal.View.Movie.MovieDetailsView;

import java.util.List;
import java.util.Optional;

import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.ErrorMessageConstants.*;
import static pl.patrykkukula.MovieReviewPortal.View.Common.Constants.RouteParametersConstants.*;

@Route("topics/add/:entityType/:entityId")
@PageTitle("Create topic")
@RolesAllowed({"ADMIN", "USER", "MODERATOR"})
@CssImport("./styles/common-styles.css")
@Slf4j
public class AddTopicView extends FormLayout implements BeforeEnterObserver {
    private final TopicServiceImpl topicService;
    private final ActorServiceImpl actorService;
    private final DirectorServiceImpl directorService;
    private final MovieServiceImpl movieService;
    private final BeanValidationBinder<TopicDto> topicBinder = new BeanValidationBinder<>(TopicDto.class);
    private final BeanValidationBinder<CommentDto> commentBinder = new BeanValidationBinder<>(CommentDto.class);
    private final Dialog errorDialog = new Dialog();
    private final Div errorMessage = new Div();
    private final Div title = new Div();
    private String entityType = "";
    private Long entityId = -1L;

    public AddTopicView(TopicServiceImpl topicService, ActorServiceImpl actorService, DirectorServiceImpl directorService, MovieServiceImpl movieService) {
        this.topicService = topicService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.movieService = movieService;
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event){
        getStyle().set("align-items", "center").set("width", "50%").set("margin", "auto");
        setResponsiveSteps(new ResponsiveStep("0", 1));
        title.addClassName("title");

        Optional<String> entityTypeOpt = event.getRouteParameters().get(ENTITY_TYPE);
        Optional<String> entityIdOpt = event.getRouteParameters().get(ENTITY_ID);

        if (entityIdOpt.isEmpty() || entityTypeOpt.isEmpty()) {
            event.rerouteToError(NullPointerException.class, NULL_POINTER_EXCEPTION_TEXT);
            return;
        }
        entityType = entityTypeOpt.get();
        try {
            entityId = Long.parseLong(entityIdOpt.get());
        }
        catch (NumberFormatException e) {
            event.rerouteToError(NumberFormatException.class, NUMBER_FORMAT_EXCEPTION_TEXT);
        }
        if (entityId < 0 || (!entityType.equals(TYPE_ACTOR) && !entityType.equals(TYPE_DIRECTOR) && !entityType.equals(TYPE_MOVIE))){
            event.rerouteToError(IllegalArgumentException.class, ILLEGAL_ARGUMENT_EXCEPTION_TEXT);
        }
        TopicDto topicDto = new TopicDto();
        CommentDto commentDto = new CommentDto();

        TextField topicTitleField = FormFields.textField("Topic title");
        topicBinder.bind(topicTitleField, "title");
        Button cancelButton = new Button();
        switch (entityType){
            case TYPE_ACTOR -> {
                ActorDto actor = actorService.fetchActorById(entityId);
                cancelButton = Buttons.cancelButton(ActorDetailsView.class, entityId);
                title.setText(("New topic on %s %s").formatted(actor.getFirstName(), actor.getLastName()));
            }
            case TYPE_DIRECTOR -> {
                DirectorDto director = directorService.fetchDirectorById(entityId);
                cancelButton = Buttons.cancelButton(DirectorDetailsView.class, entityId);
                title.setText(("New topic on %s %s").formatted(director.getFirstName(), director.getLastName()));
            }
            case TYPE_MOVIE -> {
                MovieDto movie = movieService.fetchMovieByIdVaadin(entityId);
                cancelButton = Buttons.cancelButton(MovieDetailsView.class, entityId);
                title.setText(("New topic on %s").formatted(movie.getTitle()));
            }
        }
        TextArea textAreaField = FormFields.textAreaField("Comment", 1000);
        commentBinder.bind(textAreaField, "text");

        Button addButton = addButton(entityId, entityType);

        HorizontalLayout buttonsLayout = new HorizontalLayout(addButton, cancelButton);
        buttonsLayout.getStyle().set("justify-content", "space-between");

        topicBinder.setBean(topicDto);
        commentBinder.setBean(commentDto);

        add(title, topicTitleField, textAreaField, buttonsLayout);
    }
    private Button addButton(Long entityId, String entityType) {
        Button addButton = new Button("Create topic");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> {
        try{
            if (topicBinder.isValid() && commentBinder.isValid()) {
                TopicDto topicDto = topicBinder.getBean();
                topicDto.setEntityId(entityId);
                topicDto.setEntityType(entityType);
                CommentDto commentDto = commentBinder.getBean();
                TopicDtoWithCommentDto topicDtoWithCommentDto = new TopicDtoWithCommentDto(topicDto, commentDto);
                topicService.createTopic(topicDtoWithCommentDto, entityId, entityType);
                switch (entityType){
                    case TYPE_ACTOR -> UI.getCurrent().navigate(ActorDetailsView.class, entityId);
                    case TYPE_DIRECTOR -> UI.getCurrent().navigate(DirectorDetailsView.class, entityId);
                    case TYPE_MOVIE -> UI.getCurrent().navigate(MovieDetailsView.class, entityId);
                }
            }
            else {
                List<ValidationResult> topicValidation = topicBinder.validate().getValidationErrors();
                List<ValidationResult> commentValidation = commentBinder.validate().getValidationErrors();
                topicValidation.addAll(commentValidation);
                Dialog validationErrorsDialog = CommonComponents.validationErrorsDialog(topicValidation);
                validationErrorsDialog.open();
            }
        }
        catch(ResourceNotFoundException | UsernameNotFoundException ex){
            errorMessage.removeAll();
            errorMessage.setText(ex.getMessage());
            errorDialog.add(errorMessage);
            errorDialog.open();
        }
        });
        return addButton;
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Topic;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CommentServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.ImageServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.TopicServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PagedList;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.Image.AvatarImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PageButtons;
import pl.patrykkukula.MovieReviewPortal.View.MainView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

@Slf4j
@Route("topics")
@PageTitle("Topic")
@AnonymousAllowed
@CssImport("./styles/common-styles.css")
public class TopicDetailsView extends VerticalLayout implements HasUrlParameter<Long>{
    private final TopicServiceImpl topicService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ImageServiceImpl imageService;
    private final CommentServiceImpl commentService;
    private final VerticalLayout commentDetailsLayout = new VerticalLayout();
    private PageButtons pageButtons;
    private List<CommentDtoWithUser> comments = new ArrayList<>();
    private PagedList<CommentDtoWithUser> pagedList;
    private UserEntity currentUserEntity;
    private BiConsumer<UserDetails, Long> consumer;
    private int currentSize = 0;
    private int currentPage = 0;
    private int totalPages = 0;
    private final String AVATAR_WIDTH = "74px";
    private final String AVATAR_HEIGHT = "74px";
    private final String AVATAR_WIDTH_REPLY = "54px";
    private final String AVATAR_HEIGHT_REPLY = "54px";
    private final String DIR =  "avatars";
    private final String DIR_PH = "avatars/placeholder.png";
    private final int PAGE_SIZE = 2;

    public TopicDetailsView(TopicServiceImpl topicService, UserDetailsServiceImpl userDetailsService, ImageServiceImpl imageService, CommentServiceImpl commentService) {
        this.topicService = topicService;
        this.userDetailsService = userDetailsService;
        this.imageService = imageService;
        this.commentService = commentService;
    }

    @Override
    public void setParameter(BeforeEvent event, Long topicId) {
        addClassName("main-layout");
        setAlignItems(Alignment.CENTER);
        commentDetailsLayout.addClassName("details-layout");
        commentDetailsLayout.getStyle().set("padding-bottom", "20px");
        currentUserEntity = userDetailsService.getUserEntity();

        UserDetails user = userDetailsService.getAuthenticatedUser();

        TopicDtoToDisplay topic = topicService.findTopicById(topicId);
        add(new H2(topic.getTitle()));

        comments = topic.getComments();
        currentSize = comments.size();
        List<CommentDtoWithUser> noReplyComments = getNoReplyComments();
        pagedList = new PagedList<>(noReplyComments, PAGE_SIZE);
        totalPages = pagedList.getTotalPages();

        commentDetailsLayout.setAlignItems(Alignment.START);

        pageButtons = new PageButtons(currentPage, totalPages);

        try {
            consumer = renderPage();
        } catch (IOException e) {
            event.rerouteToError(IOException.class, "Internal error loading user avatar. Please contact technical support");
        }
        pageButtons.configurePageButtons(user, topicId, pagedList, consumer);

        consumer.accept(user, topicId);
        add(commentDetailsLayout);

        add(pageButtons);
    }

    private BiConsumer<UserDetails, Long> renderPage() throws IOException {
        BiConsumer<UserDetails, Long> consumer = (user, topicId) -> {
            commentDetailsLayout.removeAll();
            currentPage = pageButtons.getCurrentPage();
            List<CommentDtoWithUser> page = pagedList.getPage(currentPage);
            int count = 1;
            for (CommentDtoWithUser comment : page) {
                VerticalLayout singleUserDetailsLayout = getSingleUserDetailsLayout(comment);
                VerticalLayout singleCommentDetailsLayout = getSingleCommentDetailsLayout(comment, user);

                HorizontalLayout row = new HorizontalLayout(singleUserDetailsLayout, singleCommentDetailsLayout);
                row.getStyle().set("border", "1px solid black").set("width", "100%");
                // Sets attribute to attach reply layout to correct row
                row.getElement().setAttribute("row-id", String.valueOf(count));
                count++;
                commentDetailsLayout.add(row);
            }
            configureNotLoggedInLayout(user, topicId);
        };
        return consumer;
    }
    /*
        Creates left side layout with user details for a comment
     */
    private VerticalLayout getSingleUserDetailsLayout(CommentDtoWithUser comment){
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("border-right", "1px solid black").set("width", "30%");
        try {
            AvatarImpl avatar = new AvatarImpl(imageService, AVATAR_WIDTH, AVATAR_HEIGHT, DIR, DIR_PH, comment.getUserId());

            Anchor userLink = new Anchor("user/%s".formatted(comment.getUserId()), comment.getAuthor());
            userLink.addClassName("no-underline");
            userLink.getStyle().set("font-size", "16px");
            Div usernameDiv = new Div(userLink);

            Div registered = new Div("Registered: ");
            registered.addClassName("bold-component");
            Div registeredDiv = new Div(registered, new Div(comment.getUserRegistered()));
            registeredDiv.getStyle().set("font-size","16px");

            Span postCount = new Span("Posts: ");
            postCount.addClassName("bold-component");
            Span postCountDiv = new Span(postCount, new Span(String.valueOf(comment.getUserCommentCount())));
            postCountDiv.getStyle().set("font-size", "16px");

            layout.add(avatar, usernameDiv, postCountDiv, registeredDiv);
            layout.setSpacing(false);

            return layout;
        }
        catch (IOException e) {
            throw new RuntimeException("Error loading user avatar", e);
        }
    }
    /*
       Creates right side layout with comment details and replies for a comment
    */
    private VerticalLayout getSingleCommentDetailsLayout(CommentDtoWithUser comment, UserDetails user){
        VerticalLayout layout = new VerticalLayout();

        Div text = new Div(comment.getText());
        text.getStyle().set("min-height", "200px");

        Div createdAt = new Div(new Span("Posted: "), new Span(comment.getCreatedAt()));
        createdAt.getStyle().set("font-size", "12px");

        Span remove = removeCommentSpan(comment, countReplies(comment.getCommentId()) > 0);
        Span update = updateCommentSpan(comment, text, layout);

        Span rightUpper = new Span(update, remove);
        HorizontalLayout leftUpper = new HorizontalLayout(createdAt);


        if (!comment.getUpdatedAt().isEmpty()) {
            Div updatedAt = new Div(new Span("Last edited: "), new Span(comment.getUpdatedAt()));
            updatedAt.getStyle().set("font-size", "12px");
            leftUpper.addComponentAtIndex(1, updatedAt);
        }
        HorizontalLayout upperLayout = new HorizontalLayout(leftUpper, rightUpper);
        upperLayout.getStyle().set("width", "100%").set("border-bottom", "1px solid grey").set("padding", "0").set("margin", "0")
                .set("justify-content", "space-between");

        Div closeReplies = new Div("Hide replies");
        closeReplies.setVisible(false);
        closeReplies.getStyle().set("cursor", "pointer").set("text-decoration", "none").set("color", "darkblue");
        Div showReplies = showRepliesDiv(layout, comment, closeReplies, user);
        closeReplies.addClickListener(e -> {
            closeReplies.setVisible(false);
            showReplies.setVisible(true);
            removeFromParent(layout, "reply-layout");
        });

        Span replySpan = replySpan(comment, user);
        Span reportSpan = reportSpan(user, comment);

        HorizontalLayout iconsLayout = new HorizontalLayout(replySpan, reportSpan);
        iconsLayout.getStyle().set("width", "100%");
        iconsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        layout.add(upperLayout, text, showReplies, closeReplies, iconsLayout);
        return layout;
    }
    /*
        Created TextArea layout to reply to comment
     */
    private void commentAreaLayout(Span replySpan, CommentDtoWithUser comment){
        try {
            HorizontalLayout row = (HorizontalLayout) replySpan.getParent().get().getParent().get().getParent().get();
            row.setWidthFull();
            int rowId = Integer.parseInt(row.getElement().getAttribute("row-id"));

            VerticalLayout commentDetailsLayout = (VerticalLayout) row.getParent().get();

            removeFromParent(commentDetailsLayout, "text-area-present");

            TextArea commentArea = commentArea();
            Span errorLabel = errorLabel();
            Span commentAreaSpan = commentAreaSpan(commentArea, errorLabel);

            Button cancel = cancelButton(commentDetailsLayout, "text-area-present");
            Button reply = replyButton(commentDetailsLayout, commentArea, comment.getTopicId(), comment.getCommentId(), errorLabel);

            HorizontalLayout buttonsLayout = new HorizontalLayout(reply, cancel);

            VerticalLayout commentLayout = new VerticalLayout(commentAreaSpan, buttonsLayout);
            commentLayout.getElement().setAttribute("text-area-present", "true");
            commentDetailsLayout.addComponentAtIndex(rowId, commentLayout);
        }
        catch (NoSuchElementException ex) {
            log.error("Error searching for parent component: {}", ex.getMessage());
            UI.getCurrent().navigate(MainView.class);
        }
    }
    /*
        Div to show replies for comment
     */
    private Div showRepliesDiv(VerticalLayout layout, CommentDtoWithUser comment, Div closeReplies, UserDetails user) {
        Long replyCount = countReplies(comment.getCommentId());
        Div showReplies = new Div();

        if (replyCount > 0) {
            showReplies.setText("Show replies(%s)".formatted(replyCount));
            showReplies.addClickListener(e -> {
                removeFromParent(layout, "update-area-present");
                removeFromParent(layout, "cancel-button-present");
                removeFromParent(layout, "update-button-present");
                showReplies.setVisible(false);
                closeReplies.setVisible(true);
                List<CommentDtoWithUser> replyComments = getReplyComments(comment.getCommentId());
                VerticalLayout verticalLayout;
                try {
                    verticalLayout = repliesLayout(replyComments, user);
                    verticalLayout.getElement().setAttribute("reply-layout", "true");
                } catch (IOException ex) {
                    throw new RuntimeException("Error loading user avatar", ex);
                }
                layout.addComponentAtIndex(2, verticalLayout);

            });
            showReplies.addClassName("link");
        }
        return showReplies;
    }
    /*
        Layout with all replies for comment
     */
    private VerticalLayout repliesLayout(List<CommentDtoWithUser> comments, UserDetails user) throws IOException {
        VerticalLayout repliesLayout = new VerticalLayout();
        for (CommentDtoWithUser comment : comments){
            repliesLayout.add(singleReplyLayout(comment, user));
        }
        return repliesLayout;
    }
    /*
        Creates single reply layout for  comment
     */
    private VerticalLayout singleReplyLayout(CommentDtoWithUser comment, UserDetails user) {
        VerticalLayout replyLayout = new VerticalLayout();
        replyLayout.getStyle().set("border-top", "0.7px solid grey").set("padding", "0").setWidth("100%");
        try {
            AvatarImpl avatar = new AvatarImpl(imageService, AVATAR_WIDTH_REPLY, AVATAR_HEIGHT_REPLY, DIR, DIR_PH, comment.getUserId());
            Anchor username = new Anchor("user/%s".formatted(comment.getUserId()),comment.getAuthor());
            username.addClassName("no-underline");
            username.getStyle().set("font-size", "16px");

            Div leftDiv = new Div(avatar, username);
            leftDiv.getStyle().set("border-right", "1px solid lightgray").set("padding-right", "50px");
            leftDiv.setHeightFull();

            Span createdAt = new Span(new Span("Posted: "), new Span(comment.getCreatedAt()));
            createdAt.getStyle().set("font-size", "12px");
            Div text = new Div(comment.getText());
            text.getStyle().set("white-space", "normal").set("word-wrap", "break-word").set("overflow-wrap", "anywhere").setWidth("100%");
            Span remove = removeCommentSpan(comment, false);

            Span update = updateCommentSpan(comment, text, replyLayout);
            Span leftUpper = new Span(createdAt);
            HorizontalLayout rightUpper = new HorizontalLayout(update, remove);
            if (!comment.getUpdatedAt().isEmpty()) {
                Span updatedAt = new Span(new Span(" Last edited: "), new Span(comment.getUpdatedAt()));
                updatedAt.getStyle().set("font-size", "12px");

                leftUpper.addComponentAtIndex(1, updatedAt);
            }

            HorizontalLayout upperLayout = new HorizontalLayout(leftUpper, rightUpper);
            upperLayout.getStyle().set("width", "100%").set("border-bottom", "1px solid grey").set("padding", "0").set("margin", "0")
                    .set("justify-content", "space-between");

            Span reportSpan = reportSpan(user, comment);

            VerticalLayout lowerRightLayout = new VerticalLayout(text, reportSpan);
            lowerRightLayout.setHeightFull();
            lowerRightLayout.getStyle().set("padding-bottom", "0");
            HorizontalLayout lowerLayout = new HorizontalLayout(leftDiv, lowerRightLayout);
            lowerLayout.setSpacing(false);
            lowerLayout.setWidthFull();

            replyLayout.add(upperLayout, lowerLayout);
        }
        catch (IOException e) {
            throw new RuntimeException("Error loading user avatar", e);
        }
        return replyLayout;
    }
    private Span replySpan(CommentDtoWithUser comment, UserDetails user){
        Icon replyIcon = VaadinIcon.REPLY.create();
        configureIcon(replyIcon);
        Span replySpan = new Span(replyIcon, new Span("Reply"));
        replySpan.getStyle().set("cursor", "pointer");
        replySpan.addClickListener(e -> commentAreaLayout(replySpan, comment));

        replySpan.setVisible(user != null);
        return replySpan;
    }
    /*
        Adds components to layout depending on if user is authenticated
     */
    private void configureNotLoggedInLayout(UserDetails user, Long topicId) {
        Anchor registerLink = new Anchor("/register", "Create account");
        Anchor loginLink = new Anchor("/login", " or login");
        Span notLoggedSpan = new Span(registerLink, loginLink);
        Div createAccount = new Div(notLoggedSpan, new Span(" to participate in discussion"));
        createAccount.getStyle().set("align-self", "center");

        TextArea commentArea = commentArea();
        Span errorLabel = errorLabel();
        Span commentAreaSpan = commentAreaSpan(commentArea, errorLabel);
        Button commentbutton = commentbutton(commentArea, topicId, errorLabel);

        if (user != null) {
            commentDetailsLayout.add(commentAreaSpan, commentbutton);
        } else {
            commentDetailsLayout.add(createAccount);
        }
    }
    private Span commentAreaSpan(TextArea commentArea, Span errorLabel){
        Span commentAreaSpan = new Span(commentArea);
        commentAreaSpan.setWidthFull();
        commentAreaSpan.add(errorLabel);
        return commentAreaSpan;
    }
    private TextArea commentArea(){
        TextArea commentArea = new TextArea();
        commentArea.setPlaceholder("Enter comment...");
        commentArea.setWidthFull();
        return commentArea;
    }
    private Span reportSpan(UserDetails userDetails, CommentDtoWithUser comment){
        Icon reportIcon = VaadinIcon.BAN.create();
        reportIcon.getStyle().set("color", "red").set("font-size", "9px");
        configureIcon(reportIcon);
        Span span = new Span("Report");
        span.getStyle().set("cursor", "pointer").set("font-size", "10px");
        Span reportSpan = new Span(reportIcon, span);
        reportSpan.getStyle().set("cursor", "pointer").set("align-self", "end");
        reportSpan.addClickListener(e -> {
        });
        boolean shouldSetVisible = currentUserEntity == null || !comment.getUserId().equals(currentUserEntity.getUserId());
        reportSpan.setVisible(userDetails != null && shouldSetVisible);
        return reportSpan;
    }
    private Button cancelButton(VerticalLayout layout, String attribute){
        Button cancel = new Button("Cancel");
        cancel.addClickListener(e ->
                removeFromParent(layout, attribute));
        return cancel;
    }
    private Button replyButton(VerticalLayout layout, TextArea commentArea, Long topicId, Long replyCommentId, Span errorLabel){
        Button reply = new Button("Post reply");

        reply.addClickListener(e -> {
            if (!commentArea.getValue().isEmpty()) {
                CommentDto comment = CommentDto.builder()
                        .text(commentArea.getValue())
                        .topicId(topicId)
                        .isReply(true)
                        .replyCommentId(replyCommentId)
                        .build();
                commentService.addComment(comment);
                UI.getCurrent().getPage().reload();
                errorLabel.setVisible(false);

                removeFromParent(layout, "text-area-present");
            }
            else errorLabel.setVisible(true);
        });
        return reply;
    }
    private Button commentbutton(TextArea commentArea, Long topicId, Span errorLabel){
        Button addComment = new Button("Add comment");

        addComment.addClickListener(e -> {
            if (!commentArea.getValue().isEmpty()) {
                CommentDto comment = CommentDto.builder()
                        .text(commentArea.getValue())
                        .topicId(topicId)
                        .isReply(false)
                        .replyCommentId(null)
                        .build();
                commentService.addComment(comment);
                UI.getCurrent().getPage().reload();
                errorLabel.setVisible(false);
            }
            else errorLabel.setVisible(true);
        });
        addComment.getStyle().set("align-self", "center");
        return addComment;
    }
    private Span removeCommentSpan(CommentDtoWithUser comment, boolean hasReplies){
        Icon icon = VaadinIcon.CLOSE.create();
        icon.getStyle().set("color", "red").set("font-size", "9px");
        Span text = new Span("Delete comment");
        text.getStyle().set("font-size", "10px");
        Span remove = new Span(icon, text);
        remove.getStyle().set("justify-self", "end").set("cursor", "pointer");
        boolean shouldSetVisible = currentUserEntity != null && comment.getUserId().equals(currentUserEntity.getUserId());
        remove.setVisible(shouldSetVisible);

        remove.addClickListener(e -> {
            commentService.removeComment(comment.getCommentId(), hasReplies);
            UI.getCurrent().getPage().reload();
        });

        return remove;
    }
    private Span updateCommentSpan(CommentDtoWithUser comment, Div text, VerticalLayout layout){
        Icon icon = VaadinIcon.EDIT.create();
        icon.getStyle().set("font-size", "9px");
        Span edit = new Span("Edit comment");
        edit.getStyle().set("font-size", "10px");
        Span editSpan = new Span(icon, edit);
        edit.getStyle().set("justify-self", "end").set("cursor", "pointer").set("padding-right", "10px");
        boolean shouldSetVisible = currentUserEntity != null && comment.getUserId().equals(currentUserEntity.getUserId());
        editSpan.setVisible(shouldSetVisible);

        edit.addClickListener(e -> {
            text.setVisible(false);
            TextArea textArea = new TextArea();
            textArea.getElement().setAttribute("update-area-present", "true");
            textArea.setValue(text.getText());
            textArea.setWidthFull();
            textArea.setMinHeight("200px");

            Span errorLabel = errorLabel();

            Button updateComment = updateButton(textArea, comment, errorLabel);

            Button cancel = cancelButton(layout, "update-area-present");
            cancel.getElement().setAttribute("cancel-button-present", "true");
            cancel.addClickListener(cv -> {
                removeFromParent(layout, "buttons-present");
                text.setVisible(true);
            });
            Span buttonsSpan = new Span(updateComment, cancel);
            Div buttonsDiv = new Div(buttonsSpan, errorLabel);
            buttonsDiv.getElement().setAttribute("buttons-present", "true");

            layout.addComponentAtIndex(2, textArea);
            layout.addComponentAtIndex(3, buttonsDiv);
        });
        return editSpan;
    }
    private Button updateButton(TextArea textArea, CommentDtoWithUser comment, Span errorLabel){
        Button updateComment = new Button("Save comment");
        updateComment.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateComment.addClickListener(ue -> {
            if (!textArea.getValue().isEmpty()) {
                errorLabel.setVisible(false);
                CommentDto commentDto = CommentDto.builder()
                        .topicId(comment.getTopicId())
                        .text(textArea.getValue())
                        .isReply(false)
                        .replyCommentId(null)
                        .build();
                try {
                    commentService.updateComment(comment.getCommentId(), commentDto);
                    UI.getCurrent().getPage().reload();
                }
                catch (IllegalResourceModifyException | ResourceNotFoundException ex){
                    Notification error = new Notification(ex.getMessage());
                    error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    error.setPosition(Notification.Position.MIDDLE);
                    error.open();
                }
            }
            else errorLabel.setVisible(true);
        });
        return updateComment;
    }
    private Span errorLabel(){
        Span errorLabel = new Span("Cannot post empty comment");
        errorLabel.getStyle().set("color", "red").set("font-size", "9px");
        errorLabel.setVisible(false);
        return errorLabel;
    }
    /*
        returns comments that are not replies
     */
    private List<CommentDtoWithUser> getNoReplyComments(){
        return comments.stream().filter(comment -> !comment.isReply()).toList();
    }
    /*
        returns comments that are replies
     */
    private List<CommentDtoWithUser> getReplyComments(Long commentId){
        return comments.stream().filter(comment -> comment.isReply() && comment.getRepliedCommentId().equals(commentId)).toList();
    }
    private Long countReplies(Long commentId){
        return comments.stream().filter(comment -> comment.isReply() && comment.getRepliedCommentId().equals(commentId)).count();
    }
    /*
        remove from given layout child with given attribute
        use only if there is only one child with given attribute
     */
    private void removeFromParent(VerticalLayout layout, String attribute){
        layout.getChildren().filter(child -> "true".equals(child.getElement().getAttribute(attribute)))
                .findFirst().ifPresent(Component::removeFromParent);
    }
    private void configureIcon(Icon icon){
        icon.addClassName("icon");
        icon.getStyle().set("margin-right", "8px");
    }
}

package pl.patrykkukula.MovieReviewPortal.View.Account;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoForUserComments;
import pl.patrykkukula.MovieReviewPortal.Service.Impl.CommentServiceImpl;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PageButtons;
import pl.patrykkukula.MovieReviewPortal.View.Common.CustomComponents.PagedList;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Route("/user/comments")
@PageTitle("Comments")
@CssImport("./styles/common-styles.css")
@AnonymousAllowed
public class UserCommentsView extends VerticalLayout implements HasUrlParameter<String> {
    private final CommentServiceImpl commentService;
    private PagedList<CommentDtoForUserComments> pagedComments = new PagedList<>(Collections.emptyList(), 10);
    private int pageNumber = 0;
    private PageButtons pageButtons = new PageButtons(0, 0);

    public UserCommentsView(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @Override
    public void setParameter(BeforeEvent event, String username) {
        addClassName("main-layout");

        List<CommentDtoForUserComments> comments = commentService.fetchAllCommentsForUserWithTopic(username);
        pagedComments.setList(comments);

        VerticalLayout commentLayout = new VerticalLayout();
        commentLayout.addClassName("details-layout");
        commentLayout.getStyle().set("border", "0.8px solid lightgrey").set("border-radius", "8px");
        commentLayout.setSpacing(false);
        renderView().accept(commentLayout);

        Span span = new Span(username + " comments");
        span.getStyle().set("align-self", "center").set("font-weight", "bold").set("font-size", "22px");

        pageButtons = new PageButtons(0, pagedComments.getTotalPages());
        pageButtons.getStyle().set("align-self", "center");

        pageButtons.setUp(commentLayout, pagedComments, renderView());

        add(span, commentLayout, pageButtons);
    }
    private Consumer<VerticalLayout> renderView(){
        return layout -> {
            layout.removeAll();

            int currentPage = pageButtons.getCurrentPage();

            List<CommentDtoForUserComments> page = pagedComments.getPage(currentPage);

            for(CommentDtoForUserComments comment : page) {
                VerticalLayout singleCommentLayout = singleCommentLayout(comment);

                layout.add(singleCommentLayout);
            }
        };
    }
    private VerticalLayout singleCommentLayout(CommentDtoForUserComments comment){
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout topicLayout = new HorizontalLayout(new Span("Topic: "));
        topicLayout.getStyle().set("font-weight", "bold").set("font-size", "18px").set("padding", "10px 20px")
                .set("border", "1px solid grey").set("width", "100%");

        Anchor topicAnchor = new Anchor("/topics/%s".formatted(comment.getTopicId()),comment.getTopicTitle());
        topicAnchor.getStyle().set("font-weight", "bold").set("font-size", "1rem")
                .set("text-decoration", "none").set("font-size", "18px").set("margin", "0");

        topicLayout.add(topicAnchor);

        VerticalLayout commentLayout = new VerticalLayout();
        commentLayout.getStyle().set("border", "1px solid grey").set("border-top", "0").set("width", "100%")
                .set("padding", "10px 20px");

        Span datesSpan = new Span("Posted: " + comment.getCreatedAt()
                + (comment.getUpdatedAt() != null ? (" Updated:" + comment.getUpdatedAt()) : ""));
        datesSpan.getStyle().set("font-size", "11px").set("border-bottom", "1px solid grey");

        Div text = new Div(comment.getText());
        text.getStyle().set("font-size", "1rem");

        commentLayout.add(datesSpan, text);

        layout.setSpacing(false);
        layout.add(topicLayout, commentLayout);
        return layout;
    }
}

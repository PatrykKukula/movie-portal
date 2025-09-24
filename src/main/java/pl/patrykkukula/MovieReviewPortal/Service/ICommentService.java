package pl.patrykkukula.MovieReviewPortal.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoForUserComments;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;

import java.util.List;

public interface ICommentService {
    /*
        common section
     */
    Long addComment(CommentDto commentDto);
    Long removeComment(Long commentId, boolean hasReplies);
    CommentDtoWithReplies fetchCommentById(Long commentId);
    List<CommentDtoWithUser> fetchAllCommentsForUser(String username);
    /*
        vaadin section
     */
    void updateCommentVaadin(Long commentId, CommentDto commentDto);
    List<CommentDtoForUserComments> fetchAllCommentsForUserWithTopic(String username);
    /*
        REST API section
     */
    void updateComment(Long commentId, String text);
}

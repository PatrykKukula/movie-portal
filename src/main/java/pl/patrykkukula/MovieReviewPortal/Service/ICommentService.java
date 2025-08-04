package pl.patrykkukula.MovieReviewPortal.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import java.util.List;

public interface ICommentService {

    Long addComment(CommentDto commentDto);
    void removeComment(Long commentId, boolean hasReplies);
//    List<CommentDtoWithUser> fetchAllCommentsForTopic(String sorted, Long topicI);
//    List<CommentDtoWithUser> fetchAllCommentsForUser(String username);
//    List<CommentDtoWithUser> fetchAllComments(String sorted);
    void updateComment(Long commentId, CommentDto commentDto);
}

package pl.patrykkukula.MovieReviewPortal.Mapper;
import pl.patrykkukula.MovieReviewPortal.Dto.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;

public class CommentMapper {

    public static CommentDtoWithUser mapToCommentDtoWithUser(Comment comment) {
        return CommentDtoWithUser.builder()
                        .text(comment.getText())
                        .commentIdInPost(comment.getCommentIdInPost())
                        .user(comment.getCreatedBy())
                        .build();
    }
}

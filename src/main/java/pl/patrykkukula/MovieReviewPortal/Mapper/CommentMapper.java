package pl.patrykkukula.MovieReviewPortal.Mapper;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;

public class CommentMapper {

    public static CommentDtoWithUser mapToCommentDtoWithUser(Comment comment) {
        return CommentDtoWithUser.builder()
                        .text(comment.getText())
                        .commentIdInPost(comment.getCommentIdInPost())
                        .topicId(comment.getTopic().getTopicId())
                        .user(comment.getCreatedBy())
                        .build();
    }
}

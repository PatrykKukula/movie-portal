package pl.patrykkukula.MovieReviewPortal.Mapper;

import lombok.extern.slf4j.Slf4j;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoForUserComments;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommentMapper {

    public static CommentDtoWithUser mapToCommentDtoWithUser(Comment comment, Map<Long, Long> commentsCount) {
        String registeredAt = comment.getUser().getRegisteredAt() != null ?
                comment.getUser().getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
        String createdAt = getCreatedAt(comment);
        String updatedAt = getUpdatedAt(comment);

        long commentCount = commentsCount.getOrDefault(comment.getUser().getUserId(), 0L);
        return CommentDtoWithUser.builder()
                        .commentId(comment.getCommentId())
                        .text(comment.getText())
                        .author(comment.getUser().getUsername())
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .commentIdInPost(comment.getCommentIdInPost())
                        .userRegistered(registeredAt)
                        .userCommentCount(commentCount)
                        .userId(comment.getUser().getUserId())
                        .isReply(comment.isReply())
                        .repliedCommentId(comment.getRepliedCommentId())
                        .topicId(comment.getTopic().getTopicId())
                        .build();
    }
    public static CommentDtoWithReplies mapToCommentDtoWithReplies(Comment comment, List<Comment> replies){
        String createdAt = getCreatedAt(comment);
        String updatedAt = getUpdatedAt(comment);

        CommentDtoWithReplies commentDtoWithReplies = CommentDtoWithReplies.builder()
                .commentId(comment.getCommentId())
                .text(comment.getText())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .author(comment.getUser().getUsername())
                .isReply(comment.isReply())
                .repliedCommentId(comment.getRepliedCommentId())
                .topicId(comment.getTopic().getTopicId())
                .build();

        List<CommentDtoWithUser> commentReplies = replies.stream().map(c -> CommentMapper.mapToCommentDtoWithUser(c, Collections.emptyMap()))
                .toList();

        commentDtoWithReplies.setReplies(commentReplies);
        return commentDtoWithReplies;
    }
    public static CommentDtoForUserComments mapCommentToCommentDtoForUserComments(Comment comment){
        return CommentDtoForUserComments.builder()
                .text(comment.getText())
                .createdAt(getCreatedAt(comment))
                .updatedAt(getUpdatedAt(comment))
                .topicId(comment.getTopic().getTopicId())
                .topicTitle(comment.getTopic().getTitle())
                .build();
    }
    private static String getCreatedAt(Comment comment){
        return comment.getCreatedAt() != null ?
                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
    }
    private static String getUpdatedAt(Comment comment){
        return comment.getUpdatedAt() != null ?
                comment.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
    }
}

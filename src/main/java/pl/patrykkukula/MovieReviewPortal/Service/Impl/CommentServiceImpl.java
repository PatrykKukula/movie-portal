package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.CommentMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.CommentRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.ICommentService;

import java.util.*;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserDetailsServiceImpl userDetailsService;

    /*
      common section
   */
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','USER', 'MODERATOR')")
    @CacheEvict(value = "topic", key = "#commentDto.topicId")
    public Long addComment(CommentDto commentDto) {
        Long topicId = commentDto.getTopicId();
        Tuple topicWithMaxCommentId = topicRepository
                .findTopicWithCurrentMaxCommentId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topic id",  String.valueOf(topicId)));
        Topic topic = topicWithMaxCommentId.get("topic", Topic.class);
        Long currentMaxCommentId = topicWithMaxCommentId.get("maxId", Long.class);
        UserEntity user = getLoggedUserEntity();

        if (commentDto.isReply()){
            Comment comment = commentRepository.findById(commentDto.getReplyCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("comment", "commentId", commentDto.getReplyCommentId().toString()));
            if (comment.isReply()) throw new IllegalStateException("Comment is reply - you can only reply to the comment that is not a reply");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .topic(topic)
                .commentIdInPost(currentMaxCommentId + 1)
                .isReply(commentDto.isReply())
                .repliedCommentId(commentDto.isReply() ? commentDto.getReplyCommentId() : null)
                .user(user)
                .build();
        try {
            Comment savedComment = commentRepository.save(comment);
            return savedComment.getCommentId();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Comment could not be added due to concurrency conflict");
        }
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','USER', 'MODERATOR')")
    @CacheEvict(value = "topic", key = "#result")
    public Long removeComment(Long commentId, boolean hasReplies){
        validateId(commentId);
        UserEntity userEntity = getLoggedUserEntity();

        Comment comment = commentRepository
                .findCommentByIdWithUserAndTopic(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));

        if (!userEntity.getUserId().equals(comment.getUser().getUserId()) )
            throw new IllegalResourceModifyException("You do not have permission to modify this comment");
        commentRepository.delete(comment);

        if (hasReplies){
            List<Comment> replies = commentRepository.findAllRepliesByCommentId(commentId);
            commentRepository.deleteAll(replies);
        }
        return comment.getTopic().getTopicId();
    }
    @Override
    public CommentDtoWithReplies fetchCommentById(Long commentId) {
        validateId(commentId);

        Comment comment = commentRepository
                .findCommentByIdWithUserAndTopic(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));

        List<Comment> replies = commentRepository.findAllRepliesByCommentId(commentId);

        return CommentMapper.mapToCommentDtoWithReplies(comment, replies);
    }
    @Override
    public List<CommentDtoWithUser> fetchAllCommentsForUser(String username) {
        List<Comment> comments = commentRepository.findAllCommentsForUserByUsername(username);
        return mapToCommentsDtoWithUser(comments);
    }
    /*
        vaadin section
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = "topic", key = "#commentId")
    public void updateCommentVaadin(Long commentId, CommentDto commentDto){
        update(commentId, commentDto.getText());
    }
    /*
        REST API section
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @CacheEvict(value = "topic", key = "#commentId")
    public void updateComment(Long commentId, String text) {
        update(commentId, text);
    }

    private void update(Long commentId, String text){
        validateId(commentId);

        UserEntity userEntity = getLoggedUserEntity();

        Comment comment = commentRepository
                .findCommentByIdWithUser(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));

        if (!userEntity.getUserId().equals(comment.getUser().getUserId()) )
            throw new IllegalResourceModifyException("You do not have permission to modify this comment");

        comment.setText(text);
        commentRepository.save(comment);
    }
    private List<CommentDtoWithUser> mapToCommentsDtoWithUser(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentMapper.mapToCommentDtoWithUser(comment, Collections.emptyMap()))
                .toList();
    }
    private UserEntity getLoggedUserEntity(){
        return userDetailsService.getLoggedUserEntity().orElseThrow(() -> new AccessDeniedException("Log in to add comment"));
    }
}

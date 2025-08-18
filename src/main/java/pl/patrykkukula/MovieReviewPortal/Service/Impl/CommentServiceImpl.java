package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.CommentRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Security.UserDetailsServiceImpl;
import pl.patrykkukula.MovieReviewPortal.Service.ICommentService;

import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @CacheEvict(value = "topic", key = "#commentDto.topicId")
    public Long addComment(CommentDto commentDto) {
        Long topicId = commentDto.getTopicId();
        Tuple topicWithMaxCommentId = topicRepository
                .findTopicWithCurrentMaxCommentId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topic id",  String.valueOf(topicId)));
        Topic topic = topicWithMaxCommentId.get("topic", Topic.class);
        Long currentMaxCommentId = topicWithMaxCommentId.get("maxId", Long.class);
        UserEntity user = userDetailsService.getLoggedUserEntity();

        Comment comment = Comment.builder()
                        .text(commentDto.getText())
                        .topic(topic)
                        .commentIdInPost(currentMaxCommentId+1)
                        .isReply(commentDto.isReply())
                        .repliedCommentId(commentDto.getReplyCommentId())
                        .user(user)
                        .build();
        try {
            Comment savedComment = commentRepository.save(comment);
            return savedComment.getCommentId();
        }
        catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Comment could not be added due to concurrency conflict");
        }
    }
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @CacheEvict(value = "topic", key = "#commentDto.topicId")
    public void removeComment(Long commentId, boolean hasReplies){
        validateId(commentId);
        Comment comment = commentRepository
                .findCommentByIdWithUser(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));
        Long userId = comment.getUser().getUserId();
        if (!canUserModify(userId)) throw new IllegalResourceModifyException("You do not have permission to delete this comment");
        commentRepository.delete(comment);

        if (hasReplies){
            List<Comment> replies = commentRepository.findAllRepliesByCommentId(commentId);
            commentRepository.deleteAll(replies);
        }
    }
//    @Override
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
//    public void removeComment(Long commentId){
//        validateId(commentId);
//        Comment comment = commentRepository
//                .findCommentByIdWithUser(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));
//        Long userId = comment.getUser().getUserId();
//        if (!canUserModify(userId)) throw new IllegalResourceModifyException("You do not have permission to delete this comment");
//        commentRepository.delete(comment);
//    }
//    @Override
//    public List<CommentDtoWithUser> fetchAllCommentsForTopic(String sorted, Long topicId){
//        validateId(topicId);
//        String validSorting = validateSorting(sorted);
//        List<Comment> comments = validSorting.equals("ASC") ? commentRepository.findAllByTopicIdSortedAsc(topicId) :
//                    commentRepository.findAllByTopicIdSortedDesc(topicId);
//        return mapToCommentsDtoWithUser(comments, "");
//    }
//    @Override
//    public List<CommentDtoWithUser> fetchAllCommentsForUser(String username) {
//        List<Comment> comments = commentRepository.findByUsername(username);
//        return mapToCommentsDtoWithUser(comments, "");
//    }
//    @Override
//    public List<CommentDtoWithUser> fetchAllComments(String sorted) {
//        String validSorting = validateSorting(sorted);
//        List<Comment> comments = validSorting.equals("ASC") ? commentRepository.findAllWithTopicOrderByIdAsc() :
//                commentRepository.findAllWithTopicOrderByIdDesc();
//        return mapToCommentsDtoWithUser(comments, "");
//    }
    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @CacheEvict(value = "topic", key = "#commentId")
    public void updateComment(Long commentId, CommentDto commentDto){
        validateId(commentId);
        Comment comment = commentRepository
                .findCommentByIdWithUser(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));
        Long userId = comment.getUser().getUserId();
        if (!canUserModify(userId)) throw new IllegalResourceModifyException("You do not have permission to modify this comment");
        comment.setText(commentDto.getText());
        commentRepository.save(comment);
    }
    // change
//    private List<CommentDtoWithUser> mapToCommentsDtoWithUser(List<Comment> comments) {
//        return comments.stream()
//                .map(comment -> CommentMapper.mapToCommentDtoWithUser(comment))
//                .toList();
//    }
    private boolean canUserModify(Long userId){
        UserEntity userEntity = userDetailsService.getLoggedUserEntity();
        return userEntity.getUserId().equals(userId) || userEntity.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ADMIN"));
    }
}

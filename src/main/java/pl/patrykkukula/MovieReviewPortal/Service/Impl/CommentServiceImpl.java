package pl.patrykkukula.MovieReviewPortal.Service.Impl;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Exception.IllegalResourceModifyException;
import pl.patrykkukula.MovieReviewPortal.Exception.ResourceNotFoundException;
import pl.patrykkukula.MovieReviewPortal.Mapper.CommentMapper;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Model.Topic;
import pl.patrykkukula.MovieReviewPortal.Model.UserEntity;
import pl.patrykkukula.MovieReviewPortal.Repository.CommentRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.TopicRepository;
import pl.patrykkukula.MovieReviewPortal.Repository.UserEntityRepository;
import pl.patrykkukula.MovieReviewPortal.Service.ICommentService;

import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateId;
import static pl.patrykkukula.MovieReviewPortal.Utils.ServiceUtils.validateSorting;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final UserEntityRepository userRepository;


    @Override
    @Transactional
    public Long addComment(CommentDto commentDto) {
        Long topicId = commentDto.getTopicId();
        Tuple topicWithMaxCommentId = topicRepository
                .findTopicWithCurrentMaxCommentId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "topic id",  String.valueOf(topicId)));
        Topic topic = topicWithMaxCommentId.get("topic", Topic.class);
        Long currentMaxCommentId = topicWithMaxCommentId.get("maxId", Long.class);
        Comment comment = Comment.builder()
                        .text(commentDto.getText())
                        .topic(topic)
                        .commentIdInPost(currentMaxCommentId+1)
                        .user(getUserEntity())
                        .build();
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getCommentId();
    }
    @Override
    public void removeComment(Long commentId){
        validateId(commentId);
        Comment comment = commentRepository
                .findCommentByIdWithUser(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "comment id", String.valueOf(commentId)));
        Long userId = comment.getUser().getUserId();
        if (!canUserModify(userId)) throw new IllegalResourceModifyException("You do not have permission to delete this comment");
        commentRepository.delete(comment);
    }
    @Override
    public List<CommentDtoWithUser> fetchAllCommentsForTopic(String sorted, Long topicId){
        validateId(topicId);
        String validSorting = validateSorting(sorted);
        List<Comment> comments = validSorting.equals("ASC") ? commentRepository.findAllByTopicIdSortedAsc(topicId) :
                    commentRepository.findAllByTopicIdSortedDesc(topicId);
        return mapToCommentsDtoWithUser(comments);
    }
    @Override
    public List<CommentDtoWithUser> fetchAllCommentsForUser(String username) {
        List<Comment> comments = commentRepository.findByUsername(username);
        return mapToCommentsDtoWithUser(comments);
    }
    @Override
    public List<CommentDtoWithUser> fetchAllComments(String sorted) {
        String validSorting = validateSorting(sorted);
        List<Comment> comments = validSorting.equals("ASC") ? commentRepository.findAllWithTopicOrderByIdAsc() :
                commentRepository.findAllWithTopicOrderByIdDesc();
        return mapToCommentsDtoWithUser(comments);
    }
    @Override
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
    private List<CommentDtoWithUser> mapToCommentsDtoWithUser(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapToCommentDtoWithUser)
                .toList();
    }
    private boolean canUserModify(Long userId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)auth.getPrincipal();
        UserEntity userEntity = userRepository.findByUsernameWithRoles(user.getUsername())
                .orElseThrow(() -> new IllegalStateException("Error during resource modification. Please try again or contact technical support"));

        return userEntity.getUserId().equals(userId) || userEntity.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ADMIN"));
    }
    private UserEntity getUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("User is not logged in");
        }
        User user = (User) auth.getPrincipal();
        return userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new ResourceNotFoundException("Account", "email", user.getUsername()));
    }
}

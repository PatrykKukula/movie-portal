package pl.patrykkukula.MovieReviewPortal.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Model.Comment;
import pl.patrykkukula.MovieReviewPortal.Service.ICommentService;
import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.List;

import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private ICommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto> createComment(@Valid @RequestBody CommentDto commentDto, HttpServletRequest request) {
        Comment comment = commentService.addComment(commentDto);
        URI location = setUri(comment.getCommentId(), request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable Long commentId) throws AccessDeniedException {
        commentService.removeComment(commentId);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @GetMapping("/{topicId}")
    public ResponseEntity<List<CommentDto>> fetchAllCommentsForTopic(@PathVariable Long topicId,
                                                                     @RequestParam(required = false, defaultValue = "ASC") String sorting) {
        return ResponseEntity.ok().body(commentService.fetchAllCommentsForTopic(sorting, topicId));
    }
    @GetMapping
    public ResponseEntity<List<CommentDto>> fetchAllComments(@RequestParam(required = false, defaultValue = "ASC") String sorting) {
        return ResponseEntity.ok().body(commentService.fetchAllComments(sorting));
    }
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseDto> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDto commentDto) throws AccessDeniedException{
        commentService.updateComment(commentId, commentDto);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

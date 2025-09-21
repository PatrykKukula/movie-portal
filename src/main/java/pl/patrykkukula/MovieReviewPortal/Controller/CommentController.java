package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithReplies;
import pl.patrykkukula.MovieReviewPortal.Dto.Comment.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Service.ICommentService;
import java.net.URI;
import java.util.List;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final ICommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto> createComment(@Valid @RequestBody CommentDto commentDto, HttpServletRequest request) {
        Long commentId = commentService.addComment(commentDto);
        URI location = setUri(commentId, request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable Long commentId){
        commentService.removeComment(commentId, true);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseDto> updateComment(@PathVariable Long commentId, @Valid @RequestBody String text){
        commentService.updateComment(commentId, text);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDtoWithReplies> fetchCommentById(@PathVariable Long commentId){
        return ResponseEntity.ok(commentService.fetchCommentById(commentId));
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDtoWithUser>> fetchAllCommentsForUser(@PathVariable String username) {
        return ResponseEntity.ok().body(commentService.fetchAllCommentsForUser(username));
    }
}

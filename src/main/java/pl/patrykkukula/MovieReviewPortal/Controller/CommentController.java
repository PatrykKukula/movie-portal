package pl.patrykkukula.MovieReviewPortal.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.CommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.CommentDtoWithUser;
import pl.patrykkukula.MovieReviewPortal.Dto.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Service.ICommentService;
import java.net.URI;
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
        Long commentId = commentService.addComment(commentDto);
        URI location = setUri(commentId, request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto> deleteComment(@PathVariable Long commentId){
        commentService.removeComment(commentId);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<CommentDtoWithUser>> fetchAllCommentsForTopic(@PathVariable Long topicId,
                                                                             @RequestParam(name = "sorted", required = false, defaultValue = "ASC") String sorting) {
        return ResponseEntity.ok().body(commentService.fetchAllCommentsForTopic(sorting, topicId));
    }
    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDtoWithUser>> fetchAllCommentsForUser(@PathVariable String username) {
        return ResponseEntity.ok().body(commentService.fetchAllCommentsForUser(username));
    }
    @GetMapping
    public ResponseEntity<List<CommentDtoWithUser>> fetchAllComments(@RequestParam(required = false, defaultValue = "ASC") String sorting) {
        return ResponseEntity.ok().body(commentService.fetchAllComments(sorting));
    }
    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseDto> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDto commentDto){
        commentService.updateComment(commentId, commentDto);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

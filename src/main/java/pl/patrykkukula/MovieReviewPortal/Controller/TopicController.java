package pl.patrykkukula.MovieReviewPortal.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.patrykkukula.MovieReviewPortal.Dto.Response.ResponseDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoBasic;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoToDisplay;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicDtoWithCommentDto;
import pl.patrykkukula.MovieReviewPortal.Dto.Topic.TopicUpdateDto;
import pl.patrykkukula.MovieReviewPortal.Service.ITopicService;
import java.net.URI;
import java.util.List;
import static pl.patrykkukula.MovieReviewPortal.Constants.ResponseConstants.*;
import static pl.patrykkukula.MovieReviewPortal.Utils.ControllerUtils.setUri;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topics")
public class TopicController {
    private final ITopicService topicService;

    @PostMapping
    public ResponseEntity<ResponseDto> createTopic(@Valid @RequestBody TopicDtoWithCommentDto topicWithComment, HttpServletRequest request) {
        Long topicId = topicService.createTopic(topicWithComment, topicWithComment.getTopic().getEntityId(), topicWithComment.getTopic().getEntityType());
        URI location = setUri(topicId, request.getRequestURI());
        return ResponseEntity.created(location).body(new ResponseDto(STATUS_201, STATUS_201_MESSAGE));
    }
    @DeleteMapping("/{topicId}")
    public ResponseEntity<ResponseDto> deleteTopic(@PathVariable Long topicId){
        topicService.deleteTopic(topicId);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDtoToDisplay> findTopicById(@PathVariable Long topicId){
        return ResponseEntity.ok(topicService.findTopicById(topicId));
    }
    @GetMapping
    public ResponseEntity<Page<TopicDtoBasic>> findAllTopics(@RequestParam(name = "sorted", required = false, defaultValue = "ASC") String sorted) {
        return ResponseEntity.ok().body(topicService.findAllTopics(0, 10, sorted, "ACTOR", 1L));
    }
    @GetMapping("/search")
    public ResponseEntity<List<TopicDtoBasic>> findTopicsByTitle(@RequestParam(name = "title") String title,
                                                           @RequestParam(name = "sorted", required = false, defaultValue = "ASC") String sorted){
       return ResponseEntity.ok().body(topicService.findTopicsByTitle(title,sorted));
    }
    @PatchMapping("/{topicId}")
    public ResponseEntity<ResponseDto> updateTopic(@PathVariable Long topicId, @Valid @RequestBody TopicUpdateDto topicDto){
        topicService.updateTopic(topicId, topicDto);
        return ResponseEntity.accepted().body(new ResponseDto(STATUS_202, STATUS_202_MESSAGE));
    }
}

package pl.patrykkukula.MovieReviewPortal.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDtoToDisplay {
    private Long topicId;
    private String title;
    private String movieTitle;
    private String author;
    private List<CommentDtoWithUser> comments = new ArrayList<>();
    private Long postCount;
}

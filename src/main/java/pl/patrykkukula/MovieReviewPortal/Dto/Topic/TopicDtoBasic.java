package pl.patrykkukula.MovieReviewPortal.Dto.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDtoBasic {

    private String title;
    private String author;
    private String movieTitle;
    private long postCount;
}

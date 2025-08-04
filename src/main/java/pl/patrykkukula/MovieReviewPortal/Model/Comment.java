package pl.patrykkukula.MovieReviewPortal.Model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(nullable = false, length = 1000)
    private String text;
    @Column(nullable = false)
    private Long commentIdInPost;
    @Column(nullable = false)
    private boolean isReply;
    private Long repliedCommentId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}

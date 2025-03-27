package pl.patrykkukula.MovieReviewPortal.Model;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.User;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Comment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private Long commentIdInPost;

    @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}

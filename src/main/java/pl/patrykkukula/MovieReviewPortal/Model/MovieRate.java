package pl.patrykkukula.MovieReviewPortal.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MovieRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieRateId;
    @Column(nullable = false)
    private Integer rate;
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}

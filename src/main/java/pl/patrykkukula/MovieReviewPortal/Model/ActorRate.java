package pl.patrykkukula.MovieReviewPortal.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ActorRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actorRateId;
    @Column(nullable = false)
    private Integer rate;
    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private Actor actor;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}

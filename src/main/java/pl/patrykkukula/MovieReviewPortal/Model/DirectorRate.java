package pl.patrykkukula.MovieReviewPortal.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DirectorRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long directorRateId;
    @Column(nullable = false)
    private Integer rate;
    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}

package pl.patrykkukula.MovieReviewPortal.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Actor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actorId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false)
    private String country;
    @Column(length = 1000)
    private String biography;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    private List<Movie> movies = new ArrayList<>();
    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ActorRate> actorRates = new ArrayList<>();

    public Double averageActorRate(){
        return actorRates.stream().collect(Collectors.averagingDouble(ActorRate::getRate));
    }
}

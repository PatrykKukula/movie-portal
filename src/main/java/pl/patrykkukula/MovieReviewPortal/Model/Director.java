package pl.patrykkukula.MovieReviewPortal.Model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Director extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long directorId;
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

    @OneToMany(mappedBy = "director", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Movie> movies = new ArrayList<>();
}

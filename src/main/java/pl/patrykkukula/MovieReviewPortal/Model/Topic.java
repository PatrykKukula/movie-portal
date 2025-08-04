package pl.patrykkukula.MovieReviewPortal.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Topic extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;
    @Column(nullable = false)
    private String title;
    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @Column(nullable = false)
    private String entityType;
    @Column(nullable = false)
    private Long entityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}

package pl.patrykkukula.MovieReviewPortal.Model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter @ToString
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Column(updatable = false, nullable = false)
    @CreatedBy
    private String createdBy;
    @Column(updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(insertable = false)
    @LastModifiedBy
    private String updatedBy;
    @Column(insertable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

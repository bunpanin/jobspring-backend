package jobspring_backend.features.Candidates.EducationLevel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "education_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "education_level_id")
    private Long educationLevelId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
        isDeleted = false;
    }
}

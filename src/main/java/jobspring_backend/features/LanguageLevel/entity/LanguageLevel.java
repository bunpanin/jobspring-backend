package jobspring_backend.features.LanguageLevel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "language_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_level_id")
    private Long languageLevelId;

    @Column(name = "language_level_name", length = 100, nullable = false)
    private String languageLevelName;

    @Column(name = "created_by", length = 100)
    private Long createdBy;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        createdDate = java.time.LocalDate.now();
        isDeleted = false;
    }
}

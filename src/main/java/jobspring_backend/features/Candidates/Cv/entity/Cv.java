package jobspring_backend.features.Candidates.Cv.entity;

import jakarta.persistence.*;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "candidate_cvs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cv_id")
    private Long cvId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false, unique = true, length = 100)
    private String storedFileName;

    @Column(name = "view_token", nullable = false, unique = true, length = 64)
    private String viewToken;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @PrePersist
    void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }
}

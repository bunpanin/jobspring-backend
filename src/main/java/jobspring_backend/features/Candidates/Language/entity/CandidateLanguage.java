package jobspring_backend.features.Candidates.Language.entity;

import jakarta.persistence.*;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.LanguageLevel.entity.LanguageLevel;
import lombok.*;

@Entity
@Table(name = "candidate_languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateLanguage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_level_id", nullable = false)
    private LanguageLevel languageLevel;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}

package jobspring_backend.features.Language.entity;

import jakarta.persistence.*;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.Skill.entity.Skill;
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

//
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}

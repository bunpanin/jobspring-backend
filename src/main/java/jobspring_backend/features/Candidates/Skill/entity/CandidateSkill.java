package jobspring_backend.features.Candidates.Skill.entity;
import jakarta.persistence.*;
import lombok.*;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;

import java.time.LocalDate;


@Entity
@Table(name = "candidate_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkill {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_date",nullable = false)
    private LocalDate createdDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if(createdDate == null){
            createdDate = LocalDate.now();
        }
        isDeleted = false;
    }
}

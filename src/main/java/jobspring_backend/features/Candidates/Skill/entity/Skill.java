package jobspring_backend.features.Candidates.Skill.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "skill_name", length = 100, nullable = false)
    private String skillName;

    @Column(name = "created_by", length = 100)
    private Long createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

//    -- Relationship --
    @OneToMany(mappedBy = "skill")
    private List<CandidateSkill> candidateSkills;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now();
        isDeleted = false;
    }
}

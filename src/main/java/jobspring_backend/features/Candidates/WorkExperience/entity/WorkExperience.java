package jobspring_backend.features.Candidates.WorkExperience.entity;
import jakarta.persistence.*;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.JobLevel.entity.JobLevel;
import jobspring_backend.features.Candidates.TypeOfExperience.entity.TypeOfExperience;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_experience_id")
    private Long workExperienceId;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @ManyToOne
    @JoinColumn(name = "job_level_id")
    private JobLevel jobLevel;

    @Column(name = "company_name", length = 100,nullable = true)
    private String companyName;

    @ManyToOne
    @JoinColumn(name = "type_of_experience_id")
    private TypeOfExperience typeOfExperience;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "is_current", nullable = true)
    private Boolean isCurrent;

    @Column(name = "job_responsibility", columnDefinition = "TEXT")
    private String jobResponsibility;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDate createdDate;

    private boolean isDeleted;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now();
        isDeleted = false;
    }
}

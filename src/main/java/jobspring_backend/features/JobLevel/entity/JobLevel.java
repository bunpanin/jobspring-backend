package jobspring_backend.features.JobLevel.entity;
import jakarta.persistence.*;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.WorkExperience.entity.WorkExperience;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "job_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_level_id")
    private Long jobLevelId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "status")
    private Boolean status;

    @OneToMany(mappedBy = "jobLevel")
    private List<Candidate> candidates;

    @OneToMany(mappedBy = "jobLevel")
    private  List<WorkExperience> workExperiences;
}
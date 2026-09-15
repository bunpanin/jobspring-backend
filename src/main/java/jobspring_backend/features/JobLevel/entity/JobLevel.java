package jobspring_backend.features.JobLevel.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
}
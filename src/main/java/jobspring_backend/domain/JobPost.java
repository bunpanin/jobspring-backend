package jobspring_backend.domain;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_code", nullable = false, unique = true, length = 50)
    private String jobCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(nullable = false, length = 150)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type",nullable = false, length = 30)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", nullable = false, length = 30)
    private WorkMode workMode;

    @Column(name = "minimum_salary", precision = 15, scale = 2)
    private BigDecimal minimumSalary;

    @Column(name = "maximum_salary", precision = 15, scale = 2)
    private BigDecimal maximumSalary;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "number_of_positions", nullable = false)
    private Integer numberOfPositions;

    @Enumerated(EnumType.STRING)
    @Column(name= "job_post_status",nullable = false, length = 30)
    private JobPostStatus jobPostStatus;

    @Column(name = "created_by_user_id", nullable = false, length = 255)
    private String createdByUserId;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

}
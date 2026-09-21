package jobspring_backend.features.Candidates.JobPost.dto.respone;
import jobspring_backend.domain.EmploymentType;
import jobspring_backend.domain.JobPost;
import jobspring_backend.domain.JobPostStatus;
import jobspring_backend.domain.WorkMode;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostResponse {

    private UUID id;
    private String jobCode;
    private String title;
    private String description;
    private String responsibilities;
    private String requirements;
    private String benefits;
    private String location;
    private EmploymentType employmentType;
    private WorkMode workMode;
    private BigDecimal minimumSalary;
    private BigDecimal maximumSalary;
    private String currency;
    private Integer numberOfPositions;
    private JobPostStatus jobPostStatus;
    private String createdByUserId;
    private LocalDateTime applicationDeadline;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobPostResponse fromEntity(JobPost jobPost) {
        return JobPostResponse.builder()
                .id(jobPost.getId())
                .jobCode(jobPost.getJobCode())
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .responsibilities(jobPost.getResponsibilities())
                .requirements(jobPost.getRequirements())
                .benefits(jobPost.getBenefits())
                .location(jobPost.getLocation())
                .employmentType(jobPost.getEmploymentType())
                .workMode(jobPost.getWorkMode())
                .minimumSalary(jobPost.getMinimumSalary())
                .maximumSalary(jobPost.getMaximumSalary())
                .currency(jobPost.getCurrency())
                .numberOfPositions(jobPost.getNumberOfPositions())
                .jobPostStatus(jobPost.getJobPostStatus())
                .createdByUserId(jobPost.getCreatedByUserId())
                .applicationDeadline(jobPost.getApplicationDeadline())
                .publishedAt(jobPost.getPublishedAt())
                .createdAt(jobPost.getCreatedAt())
                .updatedAt(jobPost.getUpdatedAt())
                .build();
    }
}
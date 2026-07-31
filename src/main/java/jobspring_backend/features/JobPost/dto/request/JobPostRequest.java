package jobspring_backend.features.JobPost.dto.request;
import jakarta.validation.constraints.*;
import jobspring_backend.domain.EmploymentType;
import jobspring_backend.domain.JobPostStatus;
import jobspring_backend.domain.WorkMode;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRequest {

    @NotBlank(message = "Job code is required")
    @Size(max = 50, message = "Job code cannot exceed 50 characters")
    private String jobCode;

    @NotBlank(message = "Job title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    private String description;

    private String responsibilities;

    private String requirements;

    private String benefits;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Work mode is required")
    private WorkMode workMode;

    @DecimalMin(value = "0.00", message = "Minimum salary cannot be negative")
    private BigDecimal minimumSalary;

    @DecimalMin(value = "0.00", message = "Maximum salary cannot be negative")
    private BigDecimal maximumSalary;

    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency cannot exceed 10 characters")
    private String currency;

    @NotNull(message = "Number of positions is required")
    @Min(value = 1, message = "Number of positions must be at least 1")
    private Integer numberOfPositions;

    @NotNull(message = "JobPost Status is required")
    private JobPostStatus jobPostStatus;

    @NotNull(message = "Application deadline is required")
    private LocalDateTime applicationDeadline;


}

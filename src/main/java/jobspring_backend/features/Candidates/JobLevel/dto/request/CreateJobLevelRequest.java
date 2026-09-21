package jobspring_backend.features.Candidates.JobLevel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateJobLevelRequest(
        @NotBlank(message = "Job level name is required")
        String name,
        @NotBlank(message = "createdBy is required")
        String createdBy
) {}
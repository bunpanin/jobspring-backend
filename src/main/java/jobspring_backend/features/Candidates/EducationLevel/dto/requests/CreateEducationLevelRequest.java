package jobspring_backend.features.Candidates.EducationLevel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEducationLevelRequest(
    @NotBlank(message = "Education level name is required")
    @Size(max = 100, message = "Education level name cannot exceed 100 characters")
    String name,
    int createdBy
) {
}

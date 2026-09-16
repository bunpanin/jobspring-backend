package jobspring_backend.features.TypeOfExperience.dto.requests;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTypeOfExperienceRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,
        @NotBlank(message = "createdBy is required")
        String createdBy
) {
}
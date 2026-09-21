package jobspring_backend.features.Candidates.TypeOfExperience.dto.requests;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTypeOfExperienceRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,
        Boolean isDeleted
) {
}
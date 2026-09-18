package jobspring_backend.features.Skill.dto.requests;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSkillRequest(

        @NotBlank(message = "Skill name is required")
        @Size(max = 100, message = "Skill name cannot exceed 100 characters")
        String skillName,
        @NotNull(message = "Created by is required")
        Long createdBy,
        String description

) {
}
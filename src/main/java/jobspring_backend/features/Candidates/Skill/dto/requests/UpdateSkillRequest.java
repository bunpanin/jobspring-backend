package jobspring_backend.features.Candidates.Skill.dto.requests;
import jakarta.validation.constraints.Size;

public record UpdateSkillRequest(

        @Size(max = 100, message = "Skill name cannot exceed 100 characters")
        String skillName,
        String description

) {
}

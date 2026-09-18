package jobspring_backend.features.Skill.dto.responses;
import org.w3c.dom.Text;

import java.time.LocalDate;

public record SkillResponse(
        Long skillId,
        String skillName,
        Long createdBy,
        String description,
        LocalDate createdDate
) {
}
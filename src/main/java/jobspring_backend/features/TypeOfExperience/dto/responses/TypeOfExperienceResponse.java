package jobspring_backend.features.TypeOfExperience.dto.responses;
import java.time.LocalDate;

public record TypeOfExperienceResponse(
        Long typeOfExperienceId,
        String name,
        String createdBy,
        LocalDate createdDate,
        Boolean isDeleted
) {
}
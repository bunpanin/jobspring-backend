package jobspring_backend.features.Candidates.EducationLevel.dto.responses;

import java.time.LocalDate;

public record EducationLevelResponse(
    Long educationLevelId,
    String name,
    String createdBy,
    LocalDate createdDate,
    boolean isDeleted
) {
}

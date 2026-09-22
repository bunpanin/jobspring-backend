package jobspring_backend.features.Candidates.Education.dto.responses;

import java.time.LocalDate;

public record EducationResponse(
    Long educationId,
    Long candidateId,
    String institution,
    Long educationLevelId,
    String educationLevelName,
    Long majorId,
    String majorName,
    String city,
    String country,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isCurrent,
    String createdBy,
    LocalDate createdDate
) {
}

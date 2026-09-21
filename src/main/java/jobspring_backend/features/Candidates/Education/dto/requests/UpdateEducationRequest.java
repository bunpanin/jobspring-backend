package jobspring_backend.features.Candidates.Education.dto.requests;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateEducationRequest(
    @Size(max = 150, message = "Institution cannot exceed 150 characters")
    String institution,
    Long educationLevelId,
    @Size(max = 150, message = "Major name cannot exceed 150 characters")
    String majorName,
    @Size(max = 100, message = "City cannot exceed 100 characters")
    String city,
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    String country,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isCurrent,
    Boolean isHidden
) {
}

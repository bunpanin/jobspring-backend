package jobspring_backend.features.Candidates.Education.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEducationRequest(
    @NotNull(message = "Candidate ID is required")
    Long candidateId,

    @NotBlank(message = "Institution is required")
    @Size(max = 150, message = "Institution cannot exceed 150 characters")
    String institution,

    @NotNull(message = "Education level is required")
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
    Boolean isHidden,
    String createdBy
) {
}

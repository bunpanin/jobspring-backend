package jobspring_backend.features.Reference.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReferenceRequest(
    @NotNull(message = "Candidate ID is required")
    Long candidateId,

    @NotBlank(message = "Reference full name is required")
    @Size(max = 150, message = "Reference full name cannot exceed 150 characters")
    String fullName,

    @Size(max = 150, message = "Position cannot exceed 150 characters")
    String position,

    @Size(max = 150, message = "Company name cannot exceed 150 characters")
    String companyName,

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    String phoneNumber,

    @Size(max = 150, message = "Email cannot exceed 150 characters")
    String email,

    String createdBy
) {
}

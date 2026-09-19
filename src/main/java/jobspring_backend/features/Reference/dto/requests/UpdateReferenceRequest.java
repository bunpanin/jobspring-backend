package jobspring_backend.features.Reference.dto.requests;

import jakarta.validation.constraints.Size;

public record UpdateReferenceRequest(
    @Size(max = 150, message = "Reference full name cannot exceed 150 characters")
    String fullName,

    @Size(max = 150, message = "Position cannot exceed 150 characters")
    String position,

    @Size(max = 150, message = "Company name cannot exceed 150 characters")
    String companyName,

    @Size(max = 30, message = "Phone number cannot exceed 30 characters")
    String phoneNumber,

    @Size(max = 150, message = "Email cannot exceed 150 characters")
    String email
) {
}

package jobspring_backend.features.Reference.dto.responses;

import java.time.LocalDate;

public record ReferenceResponse(
    Long referenceId,
    Long candidateId,
    String fullName,
    String position,
    String companyName,
    String phoneNumber,
    String email,
    String createdBy,
    LocalDate createdDate
) {
}

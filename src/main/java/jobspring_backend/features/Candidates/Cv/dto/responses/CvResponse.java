package jobspring_backend.features.Candidates.Cv.dto.responses;

import java.time.LocalDate;

public record CvResponse(
    Long cvId,
    Long candidateId,
    String originalFileName,
    String contentType,
    Long fileSize,
    boolean isPrimary,
    LocalDate createdDate,
    String viewUrl
) {
}

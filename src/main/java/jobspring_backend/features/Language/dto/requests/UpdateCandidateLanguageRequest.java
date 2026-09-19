package jobspring_backend.features.Language.dto.requests;

import jakarta.validation.constraints.Size;

public record UpdateCandidateLanguageRequest(
    @Size(max = 100, message = "Language name cannot exceed 100 characters")
    String languageName,
    Long languageLevelId
) {
}

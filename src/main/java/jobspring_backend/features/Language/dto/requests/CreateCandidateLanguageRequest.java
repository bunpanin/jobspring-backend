package jobspring_backend.features.Language.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCandidateLanguageRequest(
    @NotNull(message = "Candidate ID is required")
    Long candidateId,

    @NotBlank(message = "Language name is required")
    @Size(max = 100, message = "Language name cannot exceed 100 characters")
    String languageName,

    @NotNull(message = "Language level ID is required")
    Long languageLevelId
) {
}

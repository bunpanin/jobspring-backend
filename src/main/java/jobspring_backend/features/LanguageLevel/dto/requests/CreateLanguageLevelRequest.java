package jobspring_backend.features.LanguageLevel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLanguageLevelRequest(
    @NotBlank(message = "Language level name is required")
    @Size(max = 100, message = "Language level name cannot exceed 100 characters")
    String languageLevelName,

    @NotNull(message = "Created by is required")
    Long createdBy
) {
}

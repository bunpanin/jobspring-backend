package jobspring_backend.features.LanguageLevel.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateLanguageLevelRequest(
    @NotBlank(message = "Language level name is required")
    @Size(max = 100, message = "Language level name cannot exceed 100 characters")
    String languageLevelName
) {
}

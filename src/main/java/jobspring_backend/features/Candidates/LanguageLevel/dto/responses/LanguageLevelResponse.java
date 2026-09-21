package jobspring_backend.features.Candidates.LanguageLevel.dto.responses;

import java.time.LocalDate;

public record LanguageLevelResponse(
    Long languageLevelId,
    String languageLevelName,
    Long createdBy,
    LocalDate createdDate,
    Boolean isDeleted
) {
}

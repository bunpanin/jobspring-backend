package jobspring_backend.features.Language.dto.responses;

public record CandidateLanguageResponse(
//    Long candidateLanguageId,
    Long candidateId,
//    Long languageId,
    String languageName,
//    Long languageLevelId,
    String languageLevelName
) {
}

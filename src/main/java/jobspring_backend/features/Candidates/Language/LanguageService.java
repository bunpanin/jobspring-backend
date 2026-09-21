package jobspring_backend.features.Candidates.Language;

import jobspring_backend.features.Candidates.Language.dto.requests.CreateCandidateLanguageRequest;
import jobspring_backend.features.Candidates.Language.dto.requests.UpdateCandidateLanguageRequest;
import jobspring_backend.features.Candidates.Language.dto.responses.CandidateLanguageResponse;

import java.util.List;

public interface LanguageService {

    CandidateLanguageResponse create(CreateCandidateLanguageRequest request);
    List<CandidateLanguageResponse> createMultiple(List<CreateCandidateLanguageRequest> requests);
    List<CandidateLanguageResponse> getByCandidateId(Long candidateId);
    CandidateLanguageResponse update(
        Long candidateId,
        Long languageId,
        UpdateCandidateLanguageRequest request
    );
    void delete(Long candidateId, Long languageId);
}

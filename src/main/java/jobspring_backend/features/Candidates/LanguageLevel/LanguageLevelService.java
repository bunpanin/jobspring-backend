package jobspring_backend.features.Candidates.LanguageLevel;

import jobspring_backend.features.Candidates.LanguageLevel.dto.requests.CreateLanguageLevelRequest;
import jobspring_backend.features.Candidates.LanguageLevel.dto.requests.UpdateLanguageLevelRequest;
import jobspring_backend.features.Candidates.LanguageLevel.dto.responses.LanguageLevelResponse;

import java.util.List;

public interface LanguageLevelService {

    LanguageLevelResponse create(CreateLanguageLevelRequest request);
    List<LanguageLevelResponse> getAll();
    LanguageLevelResponse getById(Long id);
    LanguageLevelResponse update(Long id, UpdateLanguageLevelRequest request);
    void delete(Long id);
}

package jobspring_backend.features.LanguageLevel;

import jobspring_backend.features.LanguageLevel.dto.requests.CreateLanguageLevelRequest;
import jobspring_backend.features.LanguageLevel.dto.requests.UpdateLanguageLevelRequest;
import jobspring_backend.features.LanguageLevel.dto.responses.LanguageLevelResponse;

import java.util.List;

public interface LanguageLevelService {

    LanguageLevelResponse create(CreateLanguageLevelRequest request);
    List<LanguageLevelResponse> getAll();
    LanguageLevelResponse getById(Long id);
    LanguageLevelResponse update(Long id, UpdateLanguageLevelRequest request);
    void delete(Long id);
}

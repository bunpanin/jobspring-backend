package jobspring_backend.features.Candidates.EducationLevel;

import jobspring_backend.features.Candidates.EducationLevel.dto.requests.CreateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.requests.UpdateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.responses.EducationLevelResponse;

import java.util.List;

public interface EducationLevelService {

    EducationLevelResponse create(CreateEducationLevelRequest request);
    List<EducationLevelResponse> getAll();
    EducationLevelResponse getById(Long id);
    EducationLevelResponse update(Long id, UpdateEducationLevelRequest request);
    void delete(Long id);
}

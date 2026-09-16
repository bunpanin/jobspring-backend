package jobspring_backend.features.WorkExperience;
import jobspring_backend.features.WorkExperience.dto.requests.CreateWorkExperienceRequest;
import jobspring_backend.features.WorkExperience.dto.requests.UpdateWorkExperienceRequest;
import jobspring_backend.features.WorkExperience.dto.responses.WorkExperienceResponse;

import java.util.List;

public interface WorkExperienceService {

    WorkExperienceResponse create(CreateWorkExperienceRequest request);
    List<WorkExperienceResponse> getAll();
    WorkExperienceResponse getById(Long id);
    List<WorkExperienceResponse> getByCandidateId(Long candidateId);
    WorkExperienceResponse update(Long id, UpdateWorkExperienceRequest request);
    void delete(Long id);
}

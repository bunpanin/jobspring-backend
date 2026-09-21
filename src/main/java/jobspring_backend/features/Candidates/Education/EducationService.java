package jobspring_backend.features.Candidates.Education;

import jobspring_backend.features.Candidates.Education.dto.requests.CreateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.requests.UpdateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.responses.EducationResponse;
import jobspring_backend.features.Candidates.Major.dto.responses.MajorResponse;

import java.util.List;

public interface EducationService {

    EducationResponse create(CreateEducationRequest request);
    List<EducationResponse> createMultiple(List<CreateEducationRequest> requests);
    List<EducationResponse> getAll();
    EducationResponse getById(Long id);
    List<EducationResponse> getByCandidateId(Long candidateId);
    EducationResponse update(Long id, UpdateEducationRequest request);
    void delete(Long id);
}

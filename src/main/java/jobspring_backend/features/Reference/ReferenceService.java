package jobspring_backend.features.Reference;

import jobspring_backend.features.Reference.dto.requests.CreateReferenceRequest;
import jobspring_backend.features.Reference.dto.requests.UpdateReferenceRequest;
import jobspring_backend.features.Reference.dto.responses.ReferenceResponse;

import java.util.List;

public interface ReferenceService {

    ReferenceResponse create(CreateReferenceRequest request);
    List<ReferenceResponse> createMultiple(List<CreateReferenceRequest> requests);
    List<ReferenceResponse> getAll();
    ReferenceResponse getById(Long id);
    List<ReferenceResponse> getByCandidateId(Long candidateId);
    ReferenceResponse update(Long id, UpdateReferenceRequest request);
    void delete(Long id);
}

package jobspring_backend.features.Candidates.Candidate;
import jobspring_backend.features.Candidates.Candidate.dto.requests.CreateCandidateRequest;
import jobspring_backend.features.Candidates.Candidate.dto.requests.UpdateCandidateRequest;
import jobspring_backend.features.Candidates.Candidate.dto.responses.CandidateResponse;
import java.util.List;

public interface CandidateService {

    CandidateResponse create(CreateCandidateRequest request);
    List<CandidateResponse> getAll();
    CandidateResponse getById(Long id);
    CandidateResponse getByUserId(String userId);
    CandidateResponse update(Long id, UpdateCandidateRequest request);
    void delete(Long id);

}

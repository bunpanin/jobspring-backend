package jobspring_backend.features.Candidates.TypeOfExperience;
import jobspring_backend.features.Candidates.TypeOfExperience.dto.requests.CreateTypeOfExperienceRequest;
import jobspring_backend.features.Candidates.TypeOfExperience.dto.requests.UpdateTypeOfExperienceRequest;
import jobspring_backend.features.Candidates.TypeOfExperience.dto.responses.TypeOfExperienceResponse;
import java.util.List;

public interface TypeOfExperienceService {
    TypeOfExperienceResponse create(CreateTypeOfExperienceRequest request);
    List<TypeOfExperienceResponse> getAll();
    TypeOfExperienceResponse getById(Long id);
    TypeOfExperienceResponse update(Long id, UpdateTypeOfExperienceRequest request);
    void delete(Long id);

}

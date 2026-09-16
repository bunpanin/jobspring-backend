package jobspring_backend.features.TypeOfExperience;
import jobspring_backend.features.TypeOfExperience.dto.requests.CreateTypeOfExperienceRequest;
import jobspring_backend.features.TypeOfExperience.dto.requests.UpdateTypeOfExperienceRequest;
import jobspring_backend.features.TypeOfExperience.dto.responses.TypeOfExperienceResponse;
import java.util.List;

public interface TypeOfExperienceService {
    TypeOfExperienceResponse create(CreateTypeOfExperienceRequest request);
    List<TypeOfExperienceResponse> getAll();
    TypeOfExperienceResponse getById(Long id);
    TypeOfExperienceResponse update(Long id, UpdateTypeOfExperienceRequest request);
    void delete(Long id);

}

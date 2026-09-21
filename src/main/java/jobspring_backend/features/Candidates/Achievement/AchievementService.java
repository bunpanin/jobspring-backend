package jobspring_backend.features.Candidates.Achievement;

import jobspring_backend.features.Candidates.Achievement.dto.requests.CreateAchievementRequest;
import jobspring_backend.features.Candidates.Achievement.dto.requests.UpdateAchievementRequest;
import jobspring_backend.features.Candidates.Achievement.dto.responses.AchievementResponse;

import java.util.List;

public interface AchievementService {

    AchievementResponse create(CreateAchievementRequest request);
    List<AchievementResponse> createMultiple(List<CreateAchievementRequest> requests);
    List<AchievementResponse> getAll();
    AchievementResponse getById(Long id);
    List<AchievementResponse> getByCandidateId(Long candidateId);
    AchievementResponse update(Long id, UpdateAchievementRequest request);
    void delete(Long id);
}

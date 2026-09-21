package jobspring_backend.features.Candidates.Skill;
import jobspring_backend.features.Candidates.Skill.dto.requests.CreateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.requests.UpdateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.responses.SkillResponse;

import java.util.List;

public interface SkillService {

    SkillResponse create(CreateSkillRequest request);
    List<SkillResponse> createMultiple(List<CreateSkillRequest> requests);
    List<SkillResponse> getAll();
    List<SkillResponse> getByCandidateId(Long candidateId);
    SkillResponse getById(Long id);
    SkillResponse update(Long candidateId, Long skillId, UpdateSkillRequest request);
    void delete(Long candidateId, Long skillId);
    void deleteSkill(Long skillId);
}

package jobspring_backend.features.Candidates.JobLevel;

import jobspring_backend.features.Candidates.JobLevel.dto.request.CreateJobLevelRequest;
import jobspring_backend.features.Candidates.JobLevel.dto.request.UpdateJobLevelRequest;
import jobspring_backend.features.Candidates.JobLevel.dto.respone.JobLevelResponse;

import java.util.List;

public interface JobLevelService {
    JobLevelResponse create(CreateJobLevelRequest request);
    List<JobLevelResponse> getAll();
    JobLevelResponse getById(Long id);
    JobLevelResponse update(Long id, UpdateJobLevelRequest request);
    void delete(Long id);
}

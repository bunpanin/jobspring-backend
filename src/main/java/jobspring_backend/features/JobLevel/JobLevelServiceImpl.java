package jobspring_backend.features.JobLevel;

import jobspring_backend.features.JobLevel.dto.request.CreateJobLevelRequest;
import jobspring_backend.features.JobLevel.dto.request.UpdateJobLevelRequest;
import jobspring_backend.features.JobLevel.dto.respone.JobLevelResponse;
import jobspring_backend.features.JobLevel.entity.JobLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobLevelServiceImpl implements JobLevelService {

    private final JobLevelRepository jobLevelRepository;

    @Override
    @Transactional
    public JobLevelResponse create(CreateJobLevelRequest request) {
        String name = request.name().trim();
        if (jobLevelRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job level already exists"
            );
        }
        JobLevel jobLevel = JobLevel.builder()
                .name(name)
                .createdBy(request.createdBy())
                .createdDate(LocalDate.now())
                .status(true)
                .build();
        JobLevel saved = jobLevelRepository.save(jobLevel);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobLevelResponse> getAll() {
         return jobLevelRepository.findAllByStatusTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobLevelResponse getById(Long id) {
        JobLevel jobLevel = findJobLevel(id);
        return mapToResponse(jobLevel);
    }

    @Override
    @Transactional
    public JobLevelResponse update(Long id, UpdateJobLevelRequest request) {

        JobLevel jobLevel = findJobLevel(id);
        String name = request.name().trim();
        boolean exists = jobLevelRepository.existsByNameIgnoreCaseAndJobLevelIdNot(name, id);

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job level already exists"
            );
        }

        jobLevel.setName(name);

        if (request.status() != null) {
            jobLevel.setStatus(request.status());
        }

        JobLevel updated = jobLevelRepository.save(jobLevel);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        JobLevel jobLevel = findJobLevel(id);
        jobLevel.setStatus(false);
        jobLevelRepository.save(jobLevel);
    }

    private JobLevel findJobLevel(Long id) {
        return jobLevelRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Job level not found with ID: " + id
                        )
                );
    }

    private JobLevelResponse mapToResponse(JobLevel jobLevel) {
        return new JobLevelResponse(
                jobLevel.getJobLevelId(),
                jobLevel.getName(),
                jobLevel.getCreatedBy(),
                jobLevel.getCreatedDate(),
                jobLevel.getStatus()
        );
    }
}

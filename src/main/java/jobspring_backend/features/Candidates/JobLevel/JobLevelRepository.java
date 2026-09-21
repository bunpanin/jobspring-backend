package jobspring_backend.features.Candidates.JobLevel;

import jobspring_backend.features.Candidates.JobLevel.entity.JobLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobLevelRepository extends JpaRepository<JobLevel,Long> {
    List<JobLevel> findAllByStatusTrue();
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndJobLevelIdNot(String name, Long jobLevelId);
}

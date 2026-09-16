package jobspring_backend.features.WorkExperience;

import jobspring_backend.features.WorkExperience.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findAllByIsDeletedFalseOrderByWorkExperienceIdDesc();
    Optional<WorkExperience>
    findByWorkExperienceIdAndIsDeletedFalse(Long workExperienceId);
    List<WorkExperience> findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByWorkExperienceIdDesc(Long candidateId);
}

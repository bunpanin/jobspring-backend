package jobspring_backend.features.Candidates.Education;

import jobspring_backend.features.Candidates.Education.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByIsDeletedFalseOrderByEducationIdDesc();
    Optional<Education> findByEducationIdAndIsDeletedFalse(Long educationId);
    List<Education> findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByEducationIdDesc(
        Long candidateId
    );
}

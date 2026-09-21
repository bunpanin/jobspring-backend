package jobspring_backend.features.Candidates.Cv;

import jobspring_backend.features.Candidates.Cv.entity.Cv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CvRepository extends JpaRepository<Cv, Long> {

    boolean existsByCandidate_CandidateIdAndIsDeletedFalseAndIsPrimaryTrue(Long candidateId);

    List<Cv> findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(
        Long candidateId
    );

    Optional<Cv> findByCvIdAndIsDeletedFalse(Long cvId);

    Optional<Cv> findByViewTokenAndIsDeletedFalse(String viewToken);
}

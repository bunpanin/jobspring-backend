package jobspring_backend.features.Candidates.Candidate;

import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findAllByIsDeletedFalse();

    Optional<Candidate> findByCandidateIdAndIsDeletedFalse(Long candidateId);
    Optional<Candidate> findByUserIdAndIsDeletedFalse(String userId);
    boolean existsByUserIdAndIsDeletedFalse(String userId);

}

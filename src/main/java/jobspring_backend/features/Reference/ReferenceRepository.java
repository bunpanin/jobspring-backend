package jobspring_backend.features.Reference;

import jobspring_backend.features.Reference.entity.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {

    List<Reference> findAllByIsDeletedFalseOrderByReferenceIdDesc();
    Optional<Reference> findByReferenceIdAndIsDeletedFalse(Long referenceId);
    List<Reference> findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByReferenceIdDesc(
        Long candidateId
    );
}

package jobspring_backend.features.Candidate;

import jobspring_backend.features.Candidate.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

}

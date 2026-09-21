package jobspring_backend.features.Candidates.Major;

import jobspring_backend.features.Candidates.Major.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {

    Optional<Major> findByNameIgnoreCaseAndIsDeletedFalse(String name);
    List<Major> findAllByIsDeletedFalseOrderByNameAsc();
}

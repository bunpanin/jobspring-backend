package jobspring_backend.features.Candidates.EducationLevel;

import jobspring_backend.features.Candidates.EducationLevel.entity.EducationLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EducationLevelRepository extends JpaRepository<EducationLevel, Long> {

    List<EducationLevel> findAllByIsDeletedFalseOrderByNameAsc();
    Optional<EducationLevel> findByEducationLevelIdAndIsDeletedFalse(Long educationLevelId);
    Optional<EducationLevel> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndEducationLevelIdNotAndIsDeletedFalse(
        String name,
        Long educationLevelId
    );
}

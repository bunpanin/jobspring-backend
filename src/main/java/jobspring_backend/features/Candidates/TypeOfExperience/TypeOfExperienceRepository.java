package jobspring_backend.features.Candidates.TypeOfExperience;

import jobspring_backend.features.Candidates.TypeOfExperience.entity.TypeOfExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TypeOfExperienceRepository extends JpaRepository<TypeOfExperience, Long> {
    List<TypeOfExperience> findAllByIsDeletedFalse();
    Optional<TypeOfExperience> findByTypeOfExperienceIdAndIsDeletedFalse(Long id);
    boolean existsByNameIgnoreCaseAndIsDeletedFalse(String name);
    boolean existsByNameIgnoreCaseAndTypeOfExperienceIdNotAndIsDeletedFalse(String name, Long id);
}

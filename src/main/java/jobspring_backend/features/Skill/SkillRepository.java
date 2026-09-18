package jobspring_backend.features.Skill;
import jobspring_backend.features.Skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByIsDeletedFalseOrderBySkillNameAsc();
    Optional<Skill> findBySkillNameIgnoreCaseAndIsDeletedFalse(String skillName);
    Optional<Skill> findBySkillIdAndIsDeletedFalse(Long skillId);
    boolean existsBySkillNameIgnoreCaseAndIsDeletedFalse(String skillName);
    boolean existsBySkillNameIgnoreCaseAndSkillIdNotAndIsDeletedFalse(String skillName, Long skillId);
}

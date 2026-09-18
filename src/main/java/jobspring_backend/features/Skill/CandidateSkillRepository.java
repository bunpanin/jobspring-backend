package jobspring_backend.features.Skill;

import jobspring_backend.features.Skill.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill,Long> {
    boolean existsByCandidate_CandidateIdAndSkill_SkillIdAndIsDeletedFalse(Long candidateId, Long skillId);
    List<CandidateSkill> findAllByCandidate_CandidateIdAndIsDeletedFalseAndSkill_IsDeletedFalseOrderBySkill_SkillNameAsc(
        Long candidateId
    );
    Optional<CandidateSkill> findByCandidate_CandidateIdAndSkill_SkillId(Long candidateId, Long skillId);
    Optional<CandidateSkill> findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndSkill_SkillIdAndSkill_IsDeletedFalseAndIsDeletedFalse(
        Long candidateId,
        Long skillId
    );
}

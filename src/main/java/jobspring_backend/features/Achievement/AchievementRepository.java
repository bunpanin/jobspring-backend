package jobspring_backend.features.Achievement;

import jobspring_backend.features.Achievement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findAllByIsDeletedFalseOrderByAchievementIdDesc();
    Optional<Achievement> findByAchievementIdAndIsDeletedFalse(Long achievementId);
    List<Achievement> findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByAchievementIdDesc(
        Long candidateId
    );
}

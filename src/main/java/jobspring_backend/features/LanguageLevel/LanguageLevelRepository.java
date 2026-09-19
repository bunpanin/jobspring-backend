package jobspring_backend.features.LanguageLevel;

import jobspring_backend.features.LanguageLevel.entity.LanguageLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageLevelRepository extends JpaRepository<LanguageLevel, Long> {

    List<LanguageLevel> findAllByIsDeletedFalseOrderByLanguageLevelNameAsc();
    Optional<LanguageLevel> findByLanguageLevelIdAndIsDeletedFalse(Long languageLevelId);
    Optional<LanguageLevel> findByLanguageLevelNameIgnoreCase(String languageLevelName);
    boolean existsByLanguageLevelNameIgnoreCaseAndLanguageLevelIdNotAndIsDeletedFalse(
        String languageLevelName,
        Long languageLevelId
    );
}

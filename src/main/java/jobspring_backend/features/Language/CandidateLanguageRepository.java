package jobspring_backend.features.Language;

import jobspring_backend.features.Language.entity.CandidateLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateLanguageRepository extends JpaRepository<CandidateLanguage, Long> {

    Optional<CandidateLanguage> findByCandidate_CandidateIdAndLanguage_LanguageId(
        Long candidateId,
        Long languageId
    );

    Optional<CandidateLanguage> findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndLanguage_LanguageIdAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseAndIsDeletedFalse(
        Long candidateId,
        Long languageId
    );

    List<CandidateLanguage> findAllByCandidate_CandidateIdAndIsDeletedFalseAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseOrderByLanguage_LanguageNameAsc(
        Long candidateId
    );

    boolean existsByCandidate_CandidateIdAndLanguage_LanguageIdAndIsDeletedFalse(
        Long candidateId,
        Long languageId
    );
}

package jobspring_backend.features.Language;

import jobspring_backend.features.Candidate.CandidateRepository;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.Language.dto.requests.CreateCandidateLanguageRequest;
import jobspring_backend.features.Language.dto.requests.UpdateCandidateLanguageRequest;
import jobspring_backend.features.Language.dto.responses.CandidateLanguageResponse;
import jobspring_backend.features.Language.entity.CandidateLanguage;
import jobspring_backend.features.Language.entity.Language;
import jobspring_backend.features.LanguageLevel.LanguageLevelRepository;
import jobspring_backend.features.LanguageLevel.entity.LanguageLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LanguageServiceImpl implements LanguageService {

    private final CandidateLanguageRepository candidateLanguageRepository;
    private final CandidateRepository candidateRepository;
    private final LanguageRepository languageRepository;
    private final LanguageLevelRepository languageLevelRepository;

    @Override
    public CandidateLanguageResponse create(CreateCandidateLanguageRequest request) {
        return createCandidateLanguage(request);
    }

    @Override
    public List<CandidateLanguageResponse> createMultiple(List<CreateCandidateLanguageRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Languages are required"
            );
        }

        return requests.stream()
            .map(this::createCandidateLanguage)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateLanguageResponse> getByCandidateId(Long candidateId) {
        findCandidate(candidateId);

        return candidateLanguageRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseOrderByLanguage_LanguageNameAsc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public CandidateLanguageResponse update(
        Long candidateId,
        Long languageId,
        UpdateCandidateLanguageRequest request
    ) {
        CandidateLanguage candidateLanguage = findActiveCandidateLanguage(candidateId, languageId);

        if (request.languageName() != null) {
            String languageName = request.languageName().trim();
            if (languageName.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Language name is required"
                );
            }

            if (!candidateLanguage.getLanguage().getLanguageName().equalsIgnoreCase(languageName)) {
                Language targetLanguage = findOrCreateLanguage(languageName, candidateId);
                boolean alreadyAssigned = candidateLanguageRepository
                    .existsByCandidate_CandidateIdAndLanguage_LanguageIdAndIsDeletedFalse(
                        candidateId,
                        targetLanguage.getLanguageId()
                    );
                if (alreadyAssigned) {
                    throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Candidate already has this language"
                    );
                }
                candidateLanguage.setLanguage(targetLanguage);
            }
        }

        if (request.languageLevelId() != null) {
            candidateLanguage.setLanguageLevel(findLanguageLevel(request.languageLevelId()));
        }

        return mapToResponse(candidateLanguage);
    }

    @Override
    public void delete(Long candidateId, Long languageId) {
        CandidateLanguage candidateLanguage = findActiveCandidateLanguage(candidateId, languageId);
        candidateLanguage.setIsDeleted(true);
    }

    private CandidateLanguageResponse createCandidateLanguage(CreateCandidateLanguageRequest request) {
        Candidate candidate = findCandidate(request.candidateId());
        LanguageLevel languageLevel = findLanguageLevel(request.languageLevelId());
        String languageName = request.languageName().trim();
        Language language = findOrCreateLanguage(languageName, request.candidateId());

        CandidateLanguage existing = candidateLanguageRepository
            .findByCandidate_CandidateIdAndLanguage_LanguageId(
                request.candidateId(),
                language.getLanguageId()
            )
            .orElse(null);

        if (existing != null) {
            if (!Boolean.TRUE.equals(existing.getIsDeleted())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Candidate already has this language"
                );
            }

            existing.setLanguageLevel(languageLevel);
            existing.setIsDeleted(false);
            return mapToResponse(existing);
        }

        CandidateLanguage candidateLanguage = CandidateLanguage.builder()
            .candidate(candidate)
            .language(language)
            .languageLevel(languageLevel)
            .isDeleted(false)
            .build();

        return mapToResponse(candidateLanguageRepository.save(candidateLanguage));
    }

    private Candidate findCandidate(Long candidateId) {
        return candidateRepository
            .findByCandidateIdAndIsDeletedFalse(candidateId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Candidate not found"
            ));
    }

    private LanguageLevel findLanguageLevel(Long languageLevelId) {
        return languageLevelRepository
            .findByLanguageLevelIdAndIsDeletedFalse(languageLevelId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Language level not found with ID: " + languageLevelId
            ));
    }

    private Language findOrCreateLanguage(String languageName, Long candidateId) {
        return languageRepository
            .findByLanguageNameIgnoreCaseAndIsDeletedFalse(languageName)
            .orElseGet(() -> languageRepository.save(
                Language.builder()
                    .languageName(languageName)
                    .createdBy(candidateId)
                    .build()
            ));
    }

    private CandidateLanguage findActiveCandidateLanguage(Long candidateId, Long languageId) {
        return candidateLanguageRepository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndLanguage_LanguageIdAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseAndIsDeletedFalse(
                candidateId,
                languageId
            )
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Candidate language not found"
            ));
    }

    private CandidateLanguageResponse mapToResponse(CandidateLanguage candidateLanguage) {
        Language language = candidateLanguage.getLanguage();
        LanguageLevel languageLevel = candidateLanguage.getLanguageLevel();
        return new CandidateLanguageResponse(
            candidateLanguage.getId(),
            candidateLanguage.getCandidate().getCandidateId(),
            language.getLanguageId(),
            language.getLanguageName(),
            languageLevel.getLanguageLevelId(),
            languageLevel.getLanguageLevelName()
        );
    }
}

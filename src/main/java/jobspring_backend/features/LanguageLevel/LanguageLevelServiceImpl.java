package jobspring_backend.features.LanguageLevel;
import jobspring_backend.features.LanguageLevel.dto.requests.CreateLanguageLevelRequest;
import jobspring_backend.features.LanguageLevel.dto.requests.UpdateLanguageLevelRequest;
import jobspring_backend.features.LanguageLevel.dto.responses.LanguageLevelResponse;
import jobspring_backend.features.LanguageLevel.entity.LanguageLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LanguageLevelServiceImpl implements LanguageLevelService {

    private final LanguageLevelRepository languageLevelRepository;

    @Override
    @Transactional
    public LanguageLevelResponse create(CreateLanguageLevelRequest request) {
        String name = request.languageLevelName().trim();
        LanguageLevel existing = languageLevelRepository
            .findByLanguageLevelNameIgnoreCase(name)
            .orElse(null);

        if (existing != null) {
            if (!Boolean.TRUE.equals(existing.getIsDeleted())) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Language level already exists"
                );
            }

            existing.setLanguageLevelName(name);
            existing.setCreatedBy(request.createdBy());
            existing.setCreatedDate(LocalDate.now());
            existing.setIsDeleted(false);
            return mapToResponse(existing);
        }

        LanguageLevel languageLevel = LanguageLevel.builder()
            .languageLevelName(name)
            .createdBy(request.createdBy())
            .createdDate(LocalDate.now())
            .isDeleted(false)
            .build();

        return mapToResponse(languageLevelRepository.save(languageLevel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LanguageLevelResponse> getAll() {
        return languageLevelRepository
            .findAllByIsDeletedFalseOrderByLanguageLevelNameAsc()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LanguageLevelResponse getById(Long id) {
        return mapToResponse(findLanguageLevel(id));
    }

    @Override
    @Transactional
    public LanguageLevelResponse update(Long id, UpdateLanguageLevelRequest request) {
        LanguageLevel languageLevel = findLanguageLevel(id);
        String name = request.languageLevelName().trim();

        boolean duplicate = languageLevelRepository
            .existsByLanguageLevelNameIgnoreCaseAndLanguageLevelIdNotAndIsDeletedFalse(name, id);
        if (duplicate) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Language level already exists"
            );
        }

        languageLevel.setLanguageLevelName(name);
        return mapToResponse(languageLevel);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LanguageLevel languageLevel = findLanguageLevel(id);
        languageLevel.setIsDeleted(true);
    }

    private LanguageLevel findLanguageLevel(Long id) {
        return languageLevelRepository
            .findByLanguageLevelIdAndIsDeletedFalse(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Language level not found with ID: " + id
                )
            );
    }

    // Method Helper

    private LanguageLevelResponse mapToResponse(LanguageLevel languageLevel) {
        return new LanguageLevelResponse(
            languageLevel.getLanguageLevelId(),
            languageLevel.getLanguageLevelName(),
            languageLevel.getCreatedBy(),
            languageLevel.getCreatedDate(),
            languageLevel.getIsDeleted()
        );
    }
}

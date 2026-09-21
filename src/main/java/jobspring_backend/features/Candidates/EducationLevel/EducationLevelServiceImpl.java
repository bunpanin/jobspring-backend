package jobspring_backend.features.Candidates.EducationLevel;

import jobspring_backend.features.Candidates.EducationLevel.dto.requests.CreateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.requests.UpdateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.responses.EducationLevelResponse;
import jobspring_backend.features.Candidates.EducationLevel.entity.EducationLevel;
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
public class EducationLevelServiceImpl implements EducationLevelService {

    private final EducationLevelRepository educationLevelRepository;

    @Override
    public EducationLevelResponse create(CreateEducationLevelRequest request) {
        String name = request.name().trim();
        EducationLevel existing = educationLevelRepository
            .findByNameIgnoreCase(name)
            .orElse(null);

        if (existing != null) {
            if (!existing.isDeleted()) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Education level already exists"
                );
            }

            existing.setName(name);
            existing.setCreatedBy(request.createdBy());
            existing.setCreatedDate(LocalDate.now());
            existing.setDeleted(false);
            return mapToResponse(existing);
        }

        EducationLevel educationLevel = EducationLevel.builder()
            .name(name)
            .createdBy(request.createdBy())
            .isDeleted(false)
            .build();

        return mapToResponse(educationLevelRepository.save(educationLevel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationLevelResponse> getAll() {
        return educationLevelRepository
            .findAllByIsDeletedFalseOrderByNameAsc()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EducationLevelResponse getById(Long id) {
        return mapToResponse(findEducationLevel(id));
    }

    @Override
    public EducationLevelResponse update(Long id, UpdateEducationLevelRequest request) {
        EducationLevel educationLevel = findEducationLevel(id);
        String name = request.name().trim();
        boolean duplicate = educationLevelRepository
            .existsByNameIgnoreCaseAndEducationLevelIdNotAndIsDeletedFalse(name, id);
        if (duplicate) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Education level already exists"
            );
        }

        educationLevel.setName(name);
        return mapToResponse(educationLevel);
    }

    @Override
    public void delete(Long id) {
        EducationLevel educationLevel = findEducationLevel(id);
        educationLevel.setDeleted(true);
    }

    private EducationLevel findEducationLevel(Long id) {
        return educationLevelRepository
            .findByEducationLevelIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Education level not found with ID: " + id
            ));
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private EducationLevelResponse mapToResponse(EducationLevel educationLevel) {
        return new EducationLevelResponse(
            educationLevel.getEducationLevelId(),
            educationLevel.getName(),
            educationLevel.getCreatedBy(),
            educationLevel.getCreatedDate(),
            educationLevel.isDeleted()
        );
    }
}

package jobspring_backend.features.TypeOfExperience;
import jobspring_backend.features.TypeOfExperience.dto.requests.CreateTypeOfExperienceRequest;
import jobspring_backend.features.TypeOfExperience.dto.requests.UpdateTypeOfExperienceRequest;
import jobspring_backend.features.TypeOfExperience.dto.responses.TypeOfExperienceResponse;
import jobspring_backend.features.TypeOfExperience.entity.TypeOfExperience;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TypeOfExperienceServiceImpl implements TypeOfExperienceService {

    private final TypeOfExperienceRepository typeOfExperienceRepository;

    @Override
    @Transactional
    public TypeOfExperienceResponse create(CreateTypeOfExperienceRequest request) {
        String name = request.name().trim();
        if (typeOfExperienceRepository.existsByNameIgnoreCaseAndIsDeletedFalse(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Type of experience already exists"
            );
        }

        TypeOfExperience typeOfExperience = TypeOfExperience.builder()
                        .name(name)
                        .createdBy(request.createdBy())
                        .build();

        TypeOfExperience saved = typeOfExperienceRepository.save(typeOfExperience);
        return mapToResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TypeOfExperienceResponse> getAll() {

        return typeOfExperienceRepository
                .findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TypeOfExperienceResponse getById(Long id) {
        TypeOfExperience typeOfExperience = findTypeOfExperience(id);
        return mapToResponse(typeOfExperience);
    }

    @Override
    @Transactional
    public TypeOfExperienceResponse update(Long id, UpdateTypeOfExperienceRequest request) {
        TypeOfExperience typeOfExperience = findTypeOfExperience(id);
        String name = request.name().trim();
        boolean exists = typeOfExperienceRepository.existsByNameIgnoreCaseAndTypeOfExperienceIdNotAndIsDeletedFalse(name, id);
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Type of experience already exists"
            );
        }
        typeOfExperience.setName(name);
        if (request.isDeleted() != null) {
            typeOfExperience.setIsDeleted(request.isDeleted());
        }
        return mapToResponse(typeOfExperience);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TypeOfExperience typeOfExperience = findTypeOfExperience(id);
        typeOfExperience.setIsDeleted(true);
    }

    // Helper method
    private TypeOfExperience findTypeOfExperience(Long id) {
        return typeOfExperienceRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Type of experience not found with ID: " + id
                        )
                );
    }

    private TypeOfExperienceResponse mapToResponse(TypeOfExperience typeOfExperience) {
        return new TypeOfExperienceResponse(
                typeOfExperience.getTypeOfExperienceId(),
                typeOfExperience.getName(),
                typeOfExperience.getCreatedBy(),
                typeOfExperience.getCreatedDate(),
                typeOfExperience.getIsDeleted()
        );
    }
}

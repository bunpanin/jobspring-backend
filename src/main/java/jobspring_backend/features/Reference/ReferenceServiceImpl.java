package jobspring_backend.features.Reference;

import jobspring_backend.features.Candidate.CandidateRepository;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.Reference.dto.requests.CreateReferenceRequest;
import jobspring_backend.features.Reference.dto.requests.UpdateReferenceRequest;
import jobspring_backend.features.Reference.dto.responses.ReferenceResponse;
import jobspring_backend.features.Reference.entity.Reference;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class ReferenceServiceImpl implements ReferenceService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
    );

    private final ReferenceRepository referenceRepository;
    private final CandidateRepository candidateRepository;

    @Override
    public ReferenceResponse create(CreateReferenceRequest request) {
        Reference reference = buildReference(request);
        return mapToResponse(referenceRepository.save(reference));
    }

    @Override
    public List<ReferenceResponse> createMultiple(List<CreateReferenceRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "References are required"
            );
        }

        List<Reference> references = requests.stream()
            .map(this::buildReference)
            .toList();

        return referenceRepository.saveAll(references)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceResponse> getAll() {
        return referenceRepository
            .findAllByIsDeletedFalseOrderByReferenceIdDesc()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReferenceResponse getById(Long id) {
        return mapToResponse(findReference(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceResponse> getByCandidateId(Long candidateId) {
        findCandidate(candidateId);
        return referenceRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByReferenceIdDesc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public ReferenceResponse update(Long id, UpdateReferenceRequest request) {
        Reference reference = findReference(id);

        String fullName = request.fullName() == null
            ? reference.getFullName()
            : request.fullName().trim();
        validateFullName(fullName);

        String phoneNumber = request.phoneNumber() == null
            ? reference.getPhoneNumber()
            : normalizeOptional(request.phoneNumber());
        String email = request.email() == null
            ? reference.getEmail()
            : normalizeOptional(request.email());
        validateContact(phoneNumber, email);

        reference.setFullName(fullName);
        reference.setPhoneNumber(phoneNumber);
        reference.setEmail(email);

        if (request.position() != null) {
            reference.setPosition(normalizeOptional(request.position()));
        }
        if (request.companyName() != null) {
            reference.setCompanyName(normalizeOptional(request.companyName()));
        }

        return mapToResponse(reference);
    }

    @Override
    public void delete(Long id) {
        Reference reference = findReference(id);
        reference.setDeleted(true);
    }

    private Reference buildReference(CreateReferenceRequest request) {
        Candidate candidate = findCandidate(request.candidateId());
        String fullName = request.fullName().trim();
        String phoneNumber = normalizeOptional(request.phoneNumber());
        String email = normalizeOptional(request.email());

        validateFullName(fullName);
        validateContact(phoneNumber, email);

        return Reference.builder()
            .candidate(candidate)
            .fullName(fullName)
            .position(normalizeOptional(request.position()))
            .companyName(normalizeOptional(request.companyName()))
            .phoneNumber(phoneNumber)
            .email(email)
            .createdBy(normalizeOptional(request.createdBy()))
            .isDeleted(false)
            .build();
    }

    private Reference findReference(Long id) {
        return referenceRepository
            .findByReferenceIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Reference not found with ID: " + id
            ));
    }

    private Candidate findCandidate(Long candidateId) {
        return candidateRepository
            .findByCandidateIdAndIsDeletedFalse(candidateId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Candidate not found with ID: " + candidateId
            ));
    }

    private void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Reference full name is required"
            );
        }
    }

    private void validateContact(String phoneNumber, String email) {
        if (phoneNumber == null && email == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email or phone number is required"
            );
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email must be valid"
            );
        }
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ReferenceResponse mapToResponse(Reference reference) {
        return new ReferenceResponse(
            reference.getReferenceId(),
            reference.getCandidate().getCandidateId(),
            reference.getFullName(),
            reference.getPosition(),
            reference.getCompanyName(),
            reference.getPhoneNumber(),
            reference.getEmail(),
            reference.getCreatedBy(),
            reference.getCreatedDate()
        );
    }
}

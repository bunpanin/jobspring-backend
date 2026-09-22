package jobspring_backend.features.Candidates.Education;

import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.Education.dto.requests.CreateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.requests.UpdateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.responses.EducationResponse;
import jobspring_backend.features.Candidates.Major.dto.responses.MajorResponse;
import jobspring_backend.features.Candidates.Education.entity.Education;
import jobspring_backend.features.Candidates.EducationLevel.EducationLevelRepository;
import jobspring_backend.features.Candidates.EducationLevel.entity.EducationLevel;
import jobspring_backend.features.Candidates.Major.MajorRepository;
import jobspring_backend.features.Candidates.Major.entity.Major;
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
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final CandidateRepository candidateRepository;
    private final MajorRepository majorRepository;
    private final EducationLevelRepository educationLevelRepository;

    @Override
    public EducationResponse create(CreateEducationRequest request) {
        Education education = buildEducation(request);
        return mapToResponse(educationRepository.save(education));
    }

    @Override
    public List<EducationResponse> createMultiple(List<CreateEducationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Educations are required"
            );
        }

        List<Education> educations = requests.stream()
            .map(this::buildEducation)
            .toList();

        return educationRepository.saveAll(educations)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> getAll() {
        return educationRepository
            .findAllByIsDeletedFalseOrderByEducationIdDesc()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EducationResponse getById(Long id) {
        return mapToResponse(findEducation(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> getByCandidateId(Long candidateId) {
        findCandidate(candidateId);
        return educationRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByEducationIdDesc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public EducationResponse update(Long id, UpdateEducationRequest request) {
        Education education = findEducation(id);

        String institution = request.institution() == null
            ? education.getInstitution()
            : request.institution().trim();
        validateInstitution(institution);

        LocalDate startDate = request.startDate() == null
            ? education.getStartDate()
            : request.startDate();
        LocalDate endDate = request.endDate() == null
            ? education.getEndDate()
            : request.endDate();
        boolean isCurrent = request.isCurrent() == null
            ? Boolean.TRUE.equals(education.getIsCurrent())
            : request.isCurrent();
        if (isCurrent) {
            endDate = null;
        }
        validateDates(startDate, endDate, isCurrent);

        Major major = education.getMajor();
        if (request.majorName() != null) {
            major = findOrCreateMajor(request.majorName(), education.getCreatedBy());
        }
        EducationLevel educationLevel = education.getEducationLevel();
        if (request.educationLevelId() != null) {
            educationLevel = findEducationLevel(request.educationLevelId());
        }

        education.setInstitution(institution);
        education.setStartDate(startDate);
        education.setEndDate(endDate);
        education.setIsCurrent(isCurrent);
        education.setMajor(major);
        education.setEducationLevel(educationLevel);

        if (request.city() != null) {
            education.setCity(normalizeOptional(request.city()));
        }
        if (request.country() != null) {
            education.setCountry(normalizeOptional(request.country()));
        }

        return mapToResponse(education);
    }

    @Override
    public void delete(Long id) {
        Education education = findEducation(id);
        education.setDeleted(true);
    }

    private Education buildEducation(CreateEducationRequest request) {
        Candidate candidate = findCandidate(request.candidateId());
        String institution = request.institution().trim();
        validateInstitution(institution);

        boolean isCurrent = Boolean.TRUE.equals(request.isCurrent());
        LocalDate endDate = isCurrent ? null : request.endDate();
        validateDates(request.startDate(), endDate, isCurrent);

        Major major = findOrCreateMajor(request.majorName(), request.createdBy());
        EducationLevel educationLevel = findEducationLevel(request.educationLevelId());

        return Education.builder()
            .candidate(candidate)
            .institution(institution)
            .educationLevel(educationLevel)
            .major(major)
            .city(normalizeOptional(request.city()))
            .country(normalizeOptional(request.country()))
            .startDate(request.startDate())
            .endDate(endDate)
            .isCurrent(isCurrent)
            .createdBy(normalizeOptional(request.createdBy()))
            .isDeleted(false)
            .build();
    }

    private Education findEducation(Long id) {
        return educationRepository
            .findByEducationIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Education not found with ID: " + id
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

    private EducationLevel findEducationLevel(Long educationLevelId) {
        return educationLevelRepository
            .findByEducationLevelIdAndIsDeletedFalse(educationLevelId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Education level not found with ID: " + educationLevelId
            ));
    }

    private Major findOrCreateMajor(String majorName, String createdBy) {
        String normalizedName = normalizeOptional(majorName);
        if (normalizedName == null) {
            return null;
        }

        return majorRepository
            .findByNameIgnoreCaseAndIsDeletedFalse(normalizedName)
            .orElseGet(() -> majorRepository.save(
                Major.builder()
                    .name(normalizedName)
                    .createdBy(normalizeOptional(createdBy))
                    .isDeleted(false)
                    .build()
            ));
    }

    private void validateInstitution(String institution) {
        if (institution == null || institution.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Institution is required"
            );
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate, boolean isCurrent) {
        if (!isCurrent && startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "End date cannot be before start date"
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

    private EducationResponse mapToResponse(Education education) {
        Major major = education.getMajor();
        EducationLevel educationLevel = education.getEducationLevel();
        return new EducationResponse(
            education.getEducationId(),
            education.getCandidate().getCandidateId(),
            education.getInstitution(),
            educationLevel == null ? null : educationLevel.getEducationLevelId(),
            educationLevel == null ? null : educationLevel.getName(),
            major == null ? null : major.getMajorId(),
            major == null ? null : major.getName(),
            education.getCity(),
            education.getCountry(),
            education.getStartDate(),
            education.getEndDate(),
            education.getIsCurrent(),
            education.getCreatedBy(),
            education.getCreatedDate()
        );
    }
}

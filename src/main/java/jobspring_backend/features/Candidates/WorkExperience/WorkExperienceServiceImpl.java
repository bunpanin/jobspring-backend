package jobspring_backend.features.Candidates.WorkExperience;
import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.JobLevel.JobLevelRepository;
import jobspring_backend.features.Candidates.JobLevel.entity.JobLevel;
import jobspring_backend.features.Candidates.TypeOfExperience.TypeOfExperienceRepository;
import jobspring_backend.features.Candidates.TypeOfExperience.entity.TypeOfExperience;
import jobspring_backend.features.Candidates.WorkExperience.dto.requests.CreateWorkExperienceRequest;
import jobspring_backend.features.Candidates.WorkExperience.dto.requests.UpdateWorkExperienceRequest;
import jobspring_backend.features.Candidates.WorkExperience.dto.responses.WorkExperienceResponse;
import jobspring_backend.features.Candidates.WorkExperience.entity.WorkExperience;
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
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final CandidateRepository candidateRepository;
    private final JobLevelRepository jobLevelRepository;
    private final TypeOfExperienceRepository typeOfExperienceRepository;

    @Override
    @Transactional
    public WorkExperienceResponse create(CreateWorkExperienceRequest request) {
        WorkExperience workExperience = buildWorkExperience(request);
        WorkExperience saved = workExperienceRepository.save(workExperience);
        return mapToResponse(saved);

    }

    @Override
    @Transactional
    public List<WorkExperienceResponse> createMultiple(List<CreateWorkExperienceRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Work experiences are required"
            );
        }

        List<WorkExperience> workExperiences = requests.stream()
            .map(this::buildWorkExperience)
            .toList();

        return workExperienceRepository
            .saveAll(workExperiences)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkExperienceResponse> getAll() {
        return workExperienceRepository
            .findAllByIsDeletedFalseOrderByWorkExperienceIdDesc()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkExperienceResponse getById(Long id) {
        WorkExperience workExperience = findWorkExperience(id);
        return mapToResponse(workExperience);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkExperienceResponse> getByCandidateId(Long candidateId) {

        findCandidate(candidateId);
        return workExperienceRepository.findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByWorkExperienceIdDesc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional
    public WorkExperienceResponse update(Long id, UpdateWorkExperienceRequest request) {
        WorkExperience workExperience = findWorkExperience(id);

        if (request.jobTitle() != null) {
            workExperience.setJobTitle(request.jobTitle().trim());
        }

        if (request.jobLevelId() != null) {
            JobLevel jobLevel = findJobLevel(request.jobLevelId());
            workExperience.setJobLevel(jobLevel);
        }

        if (request.companyName() != null) {
            workExperience.setCompanyName(request.companyName().trim());
        }

        if (request.typeOfExperienceId() != null) {
            TypeOfExperience typeOfExperience = findTypeOfExperience(request.typeOfExperienceId());
            workExperience.setTypeOfExperience(typeOfExperience);
        }

        if (request.city() != null) {
            workExperience.setCity(request.city().trim());
        }

        if (request.country() != null) {
            workExperience.setCountry(request.country().trim());
        }

        if (request.startDate() != null) {
            workExperience.setStartDate(request.startDate());
        }

        if (request.endDate() != null) {
            workExperience.setEndDate(request.endDate());
            if(request.isCurrent() == null){
                workExperience.setIsCurrent(false);
            }
        }

        if (request.isCurrent() != null) {
            workExperience.setIsCurrent(request.isCurrent());
            if (request.isCurrent()) {
                workExperience.setEndDate(null);
            }
        }

        if (request.jobResponsibility() != null) {
            workExperience.setJobResponsibility(request.jobResponsibility());
        }

        validateDates(workExperience.getStartDate(), workExperience.getEndDate(), workExperience.getIsCurrent());

        return mapToResponse(workExperience);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        WorkExperience workExperience = findWorkExperience(id);
        workExperience.setDeleted(true);
    }


    // ------------------- Helper Methods -------------------

    public static String safeTrim(String value) {
        return value == null ? null : value.trim();
    }

    private WorkExperience findWorkExperience(Long id) {
        return workExperienceRepository.findByWorkExperienceIdAndIsDeletedFalse(id).orElseThrow(() ->
            new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Work experience not found with ID: " + id
            )
        );
    }

    private Candidate findCandidate(Long candidateId) {
        return candidateRepository.findByCandidateIdAndIsDeletedFalse(candidateId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidate not found with ID: " + candidateId
                )
            );
    }

    private JobLevel findJobLevel(Long jobLevelId) {
        JobLevel jobLevel = jobLevelRepository.findById(jobLevelId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Job level not found with ID: " + jobLevelId
                )
            );

        if (!Boolean.TRUE.equals(jobLevel.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Job level is inactive"
            );
        }

        return jobLevel;
    }

    private TypeOfExperience findTypeOfExperience(Long typeOfExperienceId) {
        return typeOfExperienceRepository.findByTypeOfExperienceIdAndIsDeletedFalse(typeOfExperienceId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Type of experience not found with ID: " + typeOfExperienceId
                )
            );
    }

    private void validateDates(LocalDate startDate, LocalDate endDate, Boolean isCurrent) {
        if (Boolean.TRUE.equals(isCurrent)) {
            return;
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "End date cannot be before start date"
            );
        }
    }

    private String trim(String value) {
        return value != null
            ? value.trim()
            : null;
    }

    private WorkExperienceResponse mapToResponse(WorkExperience workExperience) {
        JobLevel jobLevel = workExperience.getJobLevel();
        TypeOfExperience typeOfExperience = workExperience.getTypeOfExperience();
        return new WorkExperienceResponse(
                workExperience.getWorkExperienceId(),
                workExperience.getCandidate().getCandidateId(),
                workExperience.getJobTitle(),
                jobLevel != null ? jobLevel.getJobLevelId() : null,
                jobLevel != null ? jobLevel.getName() : null,
                workExperience.getCompanyName(),
                typeOfExperience != null ? typeOfExperience.getTypeOfExperienceId() : null,
                typeOfExperience != null ? typeOfExperience.getName() : null,
                workExperience.getCity(),
                workExperience.getCountry(),
                workExperience.getStartDate(),
                workExperience.getEndDate(),
                workExperience.getIsCurrent(),
                workExperience.getJobResponsibility(),
                workExperience.getCreatedBy(),
                workExperience.getCreatedDate()
        );
    }

    private WorkExperience buildWorkExperience(CreateWorkExperienceRequest request) {


        Candidate candidate = findCandidate(request.candidateId());
        JobLevel jobLevel = null;
        if (request.jobLevelId() != null) {
            jobLevel = findJobLevel(request.jobLevelId());
        }

        TypeOfExperience typeOfExperience = null;
        if (request.typeOfExperienceId() != null) {
            typeOfExperience = findTypeOfExperience(request.typeOfExperienceId());
        }

        validateDates(request.startDate(), request.endDate(), request.isCurrent());

        return WorkExperience.builder()
                .candidate(candidate)
                .jobTitle(safeTrim(request.jobTitle()))
                .jobLevel(jobLevel)
                .companyName(safeTrim(request.companyName()))
                .typeOfExperience(typeOfExperience)
                .city(trim(request.city()))
                .country(safeTrim(request.country()))
                .startDate(request.startDate())
                .createdBy(safeTrim(request.createdBy()))
                .endDate(Boolean.TRUE.equals(request.isCurrent()) ? null : request.endDate())
                .isCurrent(Boolean.TRUE.equals(request.isCurrent()))
                .jobResponsibility(request.jobResponsibility())
                .build();
    }
}

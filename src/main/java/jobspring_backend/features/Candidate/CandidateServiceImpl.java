package jobspring_backend.features.Candidate;
import jobspring_backend.features.Candidate.dto.requests.CreateCandidateRequest;
import jobspring_backend.features.Candidate.dto.requests.UpdateCandidateRequest;
import jobspring_backend.features.Candidate.dto.responses.CandidateResponse;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.JobLevel.JobLevelRepository;
import jobspring_backend.features.JobLevel.entity.JobLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final JobLevelRepository jobLevelRepository;

    @Override
    @Transactional
    public CandidateResponse create(CreateCandidateRequest request) {
        if (candidateRepository.existsByUserIdAndIsDeletedFalse(request.userId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Candidate profile already exists for this user"
            );
        }
        JobLevel jobLevel = null;
        if (request.jobLevelId() != null) {
            jobLevel = findJobLevel(request.jobLevelId());
        }
        Candidate candidate = Candidate.builder()
                .userId(request.userId())
                .gender(request.gender())
                .contactNumber(request.contactNumber())
                .dateOfBirth(request.dateOfBirth())
                .jobTitle(request.jobTitle())
                .jobLevel(jobLevel)
                .industry(request.industry())
                .address(request.address())
                .city(request.city())
                .country(request.country())
                .githubUsername(request.githubUsername())
                .linkedinUsername(request.linkedinUsername())
                .portfolio(request.portfolio())
                .description(request.description())
                .build();

        Candidate saved = candidateRepository.save(candidate);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateResponse> getAll() {
        return candidateRepository
                .findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getById(Long id) {
        Candidate candidate = findCandidate(id);
        return mapToResponse(candidate);
    }


    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getByUserId(String userId) {
        Candidate candidate = candidateRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Candidate not found for user ID: " + userId
                        )
                );
        return mapToResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateResponse update(Long id, UpdateCandidateRequest request) {
        Candidate candidate = findCandidate(id);

        if (request.gender() != null) {
            candidate.setGender(request.gender());
        }

        if (request.contactNumber() != null) {
            candidate.setContactNumber(request.contactNumber());
        }

        if (request.dateOfBirth() != null) {
            candidate.setDateOfBirth(request.dateOfBirth());
        }

        if (request.jobTitle() != null) {
            candidate.setJobTitle(request.jobTitle());
        }

        if (request.jobLevelId() != null) {
            JobLevel jobLevel = findJobLevel(request.jobLevelId());
            candidate.setJobLevel(jobLevel);
        }

        if (request.industry() != null) {
            candidate.setIndustry(request.industry());
        }

        if (request.address() != null) {
            candidate.setAddress(request.address());
        }

        if (request.city() != null) {
            candidate.setCity(request.city());
        }

        if (request.country() != null) {
            candidate.setCountry(request.country());
        }

        if (request.githubUsername() != null) {
            candidate.setGithubUsername(request.githubUsername());
        }

        if (request.linkedinUsername() != null) {
            candidate.setLinkedinUsername(request.linkedinUsername());
        }

        if (request.portfolio() != null) {
            candidate.setPortfolio(request.portfolio());
        }

        if (request.description() != null) {
            candidate.setDescription(request.description());
        }

        return mapToResponse(candidate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Candidate candidate = findCandidate(id);
        candidate.setDeleted(true);
    }


    // Helper methods

    private Candidate findCandidate(Long id) {

        return candidateRepository
                .findByCandidateIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Candidate not found with ID: " + id
                        )
                );
    }

    private JobLevel findJobLevel(Long id) {
        JobLevel jobLevel = jobLevelRepository.findById(id).orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Job level not found with ID: " + id
                        ));

        if (!Boolean.TRUE.equals(jobLevel.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job level is inactive"
            );
        }

        return jobLevel;
    }

    private CandidateResponse mapToResponse(Candidate candidate) {
        JobLevel jobLevel = candidate.getJobLevel();

        return new CandidateResponse(
                candidate.getCandidateId(),
                candidate.getUserId(),
                candidate.getGender(),
                candidate.getContactNumber(),
                candidate.getDateOfBirth(),
                candidate.getJobTitle(),
                jobLevel != null ? jobLevel.getJobLevelId() : null,
                jobLevel != null ? jobLevel.getName() : null,
                candidate.getIndustry(),
                candidate.getAddress(),
                candidate.getCity(),
                candidate.getCountry(),
                candidate.getGithubUsername(),
                candidate.getLinkedinUsername(),
                candidate.getPortfolio(),
                candidate.getDescription(),
                candidate.isDeleted(),
                candidate.getCreatedAt()
        );
    }
}

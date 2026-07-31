package jobspring_backend.features.JobPost;
import jobspring_backend.domain.JobPost;
import jobspring_backend.domain.JobPostStatus;
import jobspring_backend.features.JobPost.dto.request.JobPostRequest;
import jobspring_backend.features.JobPost.dto.respone.JobPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobPostResponse> getAllJobPosts() {
        return jobPostRepository.findAll()
                .stream()
                .map(JobPostResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostResponse getJobPostById(UUID id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job post not found"
                ));

        return JobPostResponse.fromEntity(jobPost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPostResponse> getMyJobPosts(
            String authenticatedUserId
    ) {
        validateAuthenticatedUserId(authenticatedUserId);

        return jobPostRepository.findAllByCreatedByUserIdOrderByCreatedAtDesc(
                        authenticatedUserId
                )
                .stream()
                .map(JobPostResponse::fromEntity)
                .toList();
    }

    @Override
    public JobPostResponse createJobPost(JobPostRequest request, String authenticatedUserId) {

        validateAuthenticatedUserId(authenticatedUserId);
        validateSalary(request);

        String jobCode = normalizeJobCode(request.getJobCode());

        if (jobPostRepository.existsByJobCode(jobCode)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job code already exists"
            );
        }

        JobPost jobPost = JobPost.builder()
                .jobCode(jobCode)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .responsibilities(request.getResponsibilities())
                .requirements(request.getRequirements())
                .benefits(request.getBenefits())
                .location(request.getLocation().trim())
                .employmentType(request.getEmploymentType())
                .workMode(request.getWorkMode())
                .minimumSalary(request.getMinimumSalary())
                .maximumSalary(request.getMaximumSalary())
                .currency(normalizeCurrency(request.getCurrency()))
                .numberOfPositions(request.getNumberOfPositions())
                .jobPostStatus(request.getJobPostStatus())
                .createdByUserId(authenticatedUserId)
                .createdAt(LocalDateTime.now())
                .applicationDeadline(request.getApplicationDeadline())
                .publishedAt(LocalDateTime.now())
                .build();

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        return JobPostResponse.fromEntity(savedJobPost);
    }

    @Override
    public JobPostResponse updateJobPost(UUID id, JobPostRequest request, String authenticatedUserId) {

        validateAuthenticatedUserId(authenticatedUserId);
        validateSalary(request);

        JobPost jobPost = findOwnedJobPost(id, authenticatedUserId);

        String jobCode = normalizeJobCode(request.getJobCode());

        if (jobPostRepository.existsByJobCodeAndIdNot(jobCode, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Job code already exists"
            );
        }

        jobPost.setJobCode(jobCode);
        jobPost.setTitle(request.getTitle().trim());
        jobPost.setDescription(request.getDescription());
        jobPost.setResponsibilities(request.getResponsibilities());
        jobPost.setRequirements(request.getRequirements());
        jobPost.setBenefits(request.getBenefits());
        jobPost.setLocation(request.getLocation().trim());
        jobPost.setEmploymentType(request.getEmploymentType());
        jobPost.setWorkMode(request.getWorkMode());
        jobPost.setMinimumSalary(request.getMinimumSalary());
        jobPost.setMaximumSalary(request.getMaximumSalary());
        jobPost.setCurrency(normalizeCurrency(request.getCurrency()));
        jobPost.setNumberOfPositions(request.getNumberOfPositions());
        jobPost.setApplicationDeadline(request.getApplicationDeadline());
        jobPost.setJobPostStatus(request.getJobPostStatus());
        jobPost.setUpdatedAt(LocalDateTime.now());

        JobPost updatedJobPost = jobPostRepository.save(jobPost);

        return JobPostResponse.fromEntity(updatedJobPost);
    }

    @Override
    public void deleteJobPost(UUID id, String authenticatedUserId) {

        validateAuthenticatedUserId(authenticatedUserId);

        JobPost jobPost = findOwnedJobPost(id, authenticatedUserId);

        jobPostRepository.delete(jobPost);
    }

    // Below is Helper methods
    private JobPost findOwnedJobPost(UUID id, String authenticatedUserId){
        return jobPostRepository
                .findByIdAndCreatedByUserId(id, authenticatedUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job post not found or you do not have permission"
                ));
    }

    private void validateAuthenticatedUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user is required"
            );
        }
    }

    private void validateSalary(JobPostRequest request) {
        BigDecimal minimumSalary = request.getMinimumSalary();
        BigDecimal maximumSalary = request.getMaximumSalary();

        if (
                minimumSalary != null
                        && maximumSalary != null
                        && maximumSalary.compareTo(minimumSalary) < 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum salary cannot be lower than minimum salary"
            );
        }
    }

    private String normalizeJobCode(String jobCode) {
        return jobCode
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeCurrency(String currency) {
        return currency
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}

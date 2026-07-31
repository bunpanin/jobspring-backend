package jobspring_backend.features.JobPost;
import jakarta.validation.Valid;
import jobspring_backend.features.JobPost.dto.request.JobPostRequest;
import jobspring_backend.features.JobPost.dto.respone.JobPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-posts")
@RequiredArgsConstructor
public class JobPostController {

    private final JobPostService jobPostService;

    /*
     * Public: get all job posts
     */
    @GetMapping
    public ResponseEntity<List<JobPostResponse>> getAllJobPosts() {
        return ResponseEntity.ok(jobPostService.getAllJobPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getJobPostById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                jobPostService.getJobPostById(id)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<JobPostResponse>> getMyJobPosts(
            @RequestHeader("X-User-Id") String authenticatedUserId
    ) {
        return ResponseEntity.ok(
                jobPostService.getMyJobPosts(authenticatedUserId)
        );
    }

    @PostMapping
    public ResponseEntity<JobPostResponse> createJobPost(
            @RequestHeader("X-User-Id") String authenticatedUserId,
            @Valid @RequestBody JobPostRequest request
    ) {
        JobPostResponse response = jobPostService.createJobPost(
                request,
                authenticatedUserId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostResponse> updateJobPost(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String authenticatedUserId,
            @Valid @RequestBody JobPostRequest request
    ) {
        return ResponseEntity.ok(
                jobPostService.updateJobPost(
                        id,
                        request,
                        authenticatedUserId
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPost(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String authenticatedUserId
    ) {
        jobPostService.deleteJobPost(
                id,
                authenticatedUserId
        );

        return ResponseEntity.noContent().build();
    }
}

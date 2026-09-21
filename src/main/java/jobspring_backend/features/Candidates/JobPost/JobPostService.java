package jobspring_backend.features.Candidates.JobPost;


import jobspring_backend.features.Candidates.JobPost.dto.request.JobPostRequest;
import jobspring_backend.features.Candidates.JobPost.dto.request.JobPostUpdate;
import jobspring_backend.features.Candidates.JobPost.dto.respone.JobPostResponse;

import java.util.List;
import java.util.UUID;

public interface JobPostService {

    List<JobPostResponse> getAllJobPosts();

    JobPostResponse getJobPostById(UUID id);

    List<JobPostResponse> getMyJobPosts(String authenticatedUserId);

    JobPostResponse createJobPost(
            JobPostRequest request,
            String authenticatedUserId
    );

    JobPostResponse updateJobPost(
            UUID id,
            JobPostUpdate request,
            String authenticatedUserId
    );

    void deleteJobPost(
            UUID id,
            String authenticatedUserId
    );
}
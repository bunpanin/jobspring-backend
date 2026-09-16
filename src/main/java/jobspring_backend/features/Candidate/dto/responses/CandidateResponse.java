package jobspring_backend.features.Candidate.dto.responses;

import java.time.LocalDate;

public record CandidateResponse(

        Long candidateId,
        String userId,
        String gender,
        String contactNumber,
        LocalDate dateOfBirth,
        String jobTitle,
        Long jobLevelId,
        String jobLevelName,
        String industry,
        String address,
        String city,
        String country,
        String githubUsername,
        String linkedinUsername,
        String portfolio,
        String description,
        boolean isDeleted,
        LocalDate createdAt

) {
}
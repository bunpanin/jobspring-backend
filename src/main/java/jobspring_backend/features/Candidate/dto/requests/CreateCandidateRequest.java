package jobspring_backend.features.Candidate.dto.requests;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateCandidateRequest(

        @NotNull(message = "User ID is required")
        String userId,

        @Size(max = 20)
        String gender,

        @Size(max = 20)
        String contactNumber,

        LocalDate dateOfBirth,

        @Size(max = 50)
        String jobTitle,

        Long jobLevelId,

        @Size(max = 50)
        String industry,

        @Size(max = 100)
        String address,

        @Size(max = 50)
        String city,

        @Size(max = 50)
        String country,

        @Size(max = 50)
        String githubUsername,

        @Size(max = 50)
        String linkedinUsername,

        @Size(max = 100)
        String portfolio,

        String description

) {
}
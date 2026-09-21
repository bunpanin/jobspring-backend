package jobspring_backend.features.Candidates.WorkExperience.dto.requests;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateWorkExperienceRequest(

        @NotNull(message = "Candidate ID is required")
        Long candidateId,

        @NotBlank(message = "Job title is required")
        @Size(max = 100, message = "Job title cannot exceed 100 characters")
        String jobTitle,

        Long jobLevelId,

        @Size(max = 100, message = "Company name cannot exceed 100 characters")
        String companyName,

        Long typeOfExperienceId,

        @Size(max = 50)
        String city,

        @Size(max = 50)
        String country,

        @NotBlank(message = "Created by is required")
        String createdBy,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        Boolean isCurrent,

        String jobResponsibility

) {
}
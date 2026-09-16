package jobspring_backend.features.WorkExperience.dto.requests;

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

        @NotBlank(message = "Company name is required")
        @Size(max = 100, message = "Company name cannot exceed 100 characters")
        String companyName,

        Long typeOfExperienceId,

        @NotBlank(message = "City is required")
        @Size(max = 50)
        String city,

        @Size(max = 50)
        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Created by is required")
        String createdBy,

        LocalDate startDate,

        LocalDate endDate,

        Boolean isCurrent,

        String jobResponsibility

) {
}
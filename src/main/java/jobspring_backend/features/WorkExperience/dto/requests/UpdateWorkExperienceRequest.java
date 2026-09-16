package jobspring_backend.features.WorkExperience.dto.requests;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateWorkExperienceRequest(

        @Size(max = 100)
        String jobTitle,
        Long jobLevelId,
        @Size(max = 100)
        String companyName,
        Long typeOfExperienceId,
        @Size(max = 50)
        String city,
        @Size(max = 50)
        String country,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        String jobResponsibility
) {
}
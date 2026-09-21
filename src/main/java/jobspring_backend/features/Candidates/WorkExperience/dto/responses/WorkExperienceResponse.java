package jobspring_backend.features.Candidates.WorkExperience.dto.responses;

import java.time.LocalDate;

public record WorkExperienceResponse(

        Long workExperienceId,
        Long candidateId,
        String jobTitle,
        Long jobLevelId,
        String jobLevelName,
        String companyName,
        Long typeOfExperienceId,
        String typeOfExperienceName,
        String city,
        String country,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        String jobResponsibility,
        String createdBy,
        LocalDate createdDate

) {
}
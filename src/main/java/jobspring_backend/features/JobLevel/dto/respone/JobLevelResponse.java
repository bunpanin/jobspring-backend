package jobspring_backend.features.JobLevel.dto.respone;

import java.time.LocalDate;

public record JobLevelResponse(
        Long jobLevelId,
        String name,
        String createdBy,
        LocalDate createdDate,
        Boolean status

) {
}

package jobspring_backend.features.Achievement.dto.responses;

import java.time.LocalDate;

public record AchievementResponse(
    Long achievementId,
    Long candidateId,
    String title,
    LocalDate achievementDate,
    String description,
    String createdBy,
    LocalDate createdDate
) {
}

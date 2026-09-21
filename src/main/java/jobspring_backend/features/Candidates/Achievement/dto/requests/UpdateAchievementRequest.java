package jobspring_backend.features.Candidates.Achievement.dto.requests;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateAchievementRequest(
    @Size(max = 150, message = "Achievement title cannot exceed 150 characters")
    String title,
    LocalDate achievementDate,
    String description
) {
}

package jobspring_backend.features.Achievement.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateAchievementRequest(
    @NotNull(message = "Candidate ID is required")
    Long candidateId,

    @NotBlank(message = "Achievement title is required")
    @Size(max = 150, message = "Achievement title cannot exceed 150 characters")
    String title,

    @NotNull(message = "Achievement date is required")
    LocalDate achievementDate,

    String description,
    String createdBy
) {
}

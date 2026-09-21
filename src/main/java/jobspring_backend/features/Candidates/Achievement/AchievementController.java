package jobspring_backend.features.Candidates.Achievement;

import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.Achievement.dto.requests.CreateAchievementRequest;
import jobspring_backend.features.Candidates.Achievement.dto.requests.UpdateAchievementRequest;
import jobspring_backend.features.Candidates.Achievement.dto.responses.AchievementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @PostMapping
    public ResponseEntity<AchievementResponse> create(
        @Valid @RequestBody CreateAchievementRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(achievementService.create(request));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<AchievementResponse>> createMultiple(
        @Valid @RequestBody List<CreateAchievementRequest> requests
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(achievementService.createMultiple(requests));
    }

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAll() {
        return ResponseEntity.ok(achievementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(achievementService.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<AchievementResponse>> getByCandidateId(
        @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(achievementService.getByCandidateId(candidateId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AchievementResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateAchievementRequest request
    ) {
        return ResponseEntity.ok(achievementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        achievementService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

package jobspring_backend.features.Candidates.Skill;
import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.Skill.dto.requests.CreateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.requests.UpdateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.responses.SkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillResponse> create(@Valid @RequestBody CreateSkillRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(skillService.create(request));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<SkillResponse>> createMultiple(@Valid @RequestBody List<CreateSkillRequest> requests) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(skillService.createMultiple(requests));
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAll() {
        return ResponseEntity.ok(skillService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<SkillResponse>> getByCandidateId(@PathVariable Long candidateId) {
        return ResponseEntity.ok(skillService.getByCandidateId(candidateId));
    }

    @PutMapping("/candidate/{candidateId}/{skillId}")
    public ResponseEntity<SkillResponse> update(
        @PathVariable Long candidateId,
        @PathVariable Long skillId,
        @Valid @RequestBody UpdateSkillRequest request
    ) {
        return ResponseEntity.ok(skillService.update(candidateId, skillId, request));
    }

    @DeleteMapping("/candidate/{candidateId}/{skillId}")
    public ResponseEntity<Void> delete(
        @PathVariable Long candidateId,
        @PathVariable Long skillId
    ) {
        skillService.delete(candidateId, skillId);
        return ResponseEntity
            .noContent()
            .build();
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long skillId) {
        skillService.deleteSkill(skillId);
        return ResponseEntity
            .noContent()
            .build();
    }
}

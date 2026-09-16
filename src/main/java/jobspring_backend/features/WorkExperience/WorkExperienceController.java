package jobspring_backend.features.WorkExperience;
import jakarta.validation.Valid;
import jobspring_backend.features.WorkExperience.dto.requests.CreateWorkExperienceRequest;
import jobspring_backend.features.WorkExperience.dto.requests.UpdateWorkExperienceRequest;
import jobspring_backend.features.WorkExperience.dto.responses.WorkExperienceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-experiences")
@RequiredArgsConstructor
public class WorkExperienceController {

    private final WorkExperienceService service;

    @PostMapping
    public ResponseEntity<WorkExperienceResponse> create(@Valid @RequestBody CreateWorkExperienceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<WorkExperienceResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkExperienceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<WorkExperienceResponse>> getByCandidateId(@PathVariable Long candidateId) {
        return ResponseEntity.ok(service.getByCandidateId(candidateId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkExperienceResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateWorkExperienceRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

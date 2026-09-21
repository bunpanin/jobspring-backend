package jobspring_backend.features.Candidates.Education;

import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.Education.dto.requests.CreateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.requests.UpdateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.responses.EducationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<EducationResponse> create(
        @Valid @RequestBody CreateEducationRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(educationService.create(request));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<EducationResponse>> createMultiple(
        @Valid @RequestBody List<CreateEducationRequest> requests
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(educationService.createMultiple(requests));
    }

    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAll() {
        return ResponseEntity.ok(educationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(educationService.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<EducationResponse>> getByCandidateId(
        @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(educationService.getByCandidateId(candidateId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateEducationRequest request
    ) {
        return ResponseEntity.ok(educationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        educationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

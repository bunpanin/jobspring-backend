package jobspring_backend.features.Candidates.Reference;

import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.Reference.dto.requests.CreateReferenceRequest;
import jobspring_backend.features.Candidates.Reference.dto.requests.UpdateReferenceRequest;
import jobspring_backend.features.Candidates.Reference.dto.responses.ReferenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/references")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    @PostMapping
    public ResponseEntity<ReferenceResponse> create(
        @Valid @RequestBody CreateReferenceRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(referenceService.create(request));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<ReferenceResponse>> createMultiple(
        @Valid @RequestBody List<CreateReferenceRequest> requests
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(referenceService.createMultiple(requests));
    }

    @GetMapping
    public ResponseEntity<List<ReferenceResponse>> getAll() {
        return ResponseEntity.ok(referenceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferenceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(referenceService.getById(id));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ReferenceResponse>> getByCandidateId(
        @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(referenceService.getByCandidateId(candidateId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferenceResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateReferenceRequest request
    ) {
        return ResponseEntity.ok(referenceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        referenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

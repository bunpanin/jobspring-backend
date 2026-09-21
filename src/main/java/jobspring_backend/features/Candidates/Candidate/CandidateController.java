package jobspring_backend.features.Candidates.Candidate;
import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.Candidate.dto.requests.CreateCandidateRequest;
import jobspring_backend.features.Candidates.Candidate.dto.requests.UpdateCandidateRequest;
import jobspring_backend.features.Candidates.Candidate.dto.responses.CandidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService service;

    private final CandidateService candidateService;


    @PostMapping
    public ResponseEntity<CandidateResponse> create(@Valid @RequestBody CreateCandidateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CandidateResponse>> getAll() {
        return ResponseEntity.ok(candidateService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CandidateResponse> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(candidateService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCandidateRequest request) {
        return ResponseEntity.ok(candidateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

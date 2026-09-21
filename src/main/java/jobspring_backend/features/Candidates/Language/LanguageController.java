package jobspring_backend.features.Candidates.Language;

import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.Language.dto.requests.CreateCandidateLanguageRequest;
import jobspring_backend.features.Candidates.Language.dto.requests.UpdateCandidateLanguageRequest;
import jobspring_backend.features.Candidates.Language.dto.responses.CandidateLanguageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping
    public ResponseEntity<CandidateLanguageResponse> create(
        @Valid @RequestBody CreateCandidateLanguageRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(languageService.create(request));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<CandidateLanguageResponse>> createMultiple(
        @Valid @RequestBody List<CreateCandidateLanguageRequest> requests
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(languageService.createMultiple(requests));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CandidateLanguageResponse>> getByCandidateId(
        @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(languageService.getByCandidateId(candidateId));
    }

    @PutMapping("/candidate/{candidateId}/{languageId}")
    public ResponseEntity<CandidateLanguageResponse> update(
        @PathVariable Long candidateId,
        @PathVariable Long languageId,
        @Valid @RequestBody UpdateCandidateLanguageRequest request
    ) {
        return ResponseEntity.ok(languageService.update(candidateId, languageId, request));
    }

    @DeleteMapping("/candidate/{candidateId}/{languageId}")
    public ResponseEntity<Void> delete(
        @PathVariable Long candidateId,
        @PathVariable Long languageId
    ) {
        languageService.delete(candidateId, languageId);
        return ResponseEntity.noContent().build();
    }

}

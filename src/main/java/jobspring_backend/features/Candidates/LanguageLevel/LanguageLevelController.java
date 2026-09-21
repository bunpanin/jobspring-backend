package jobspring_backend.features.Candidates.LanguageLevel;
import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.LanguageLevel.dto.requests.CreateLanguageLevelRequest;
import jobspring_backend.features.Candidates.LanguageLevel.dto.requests.UpdateLanguageLevelRequest;
import jobspring_backend.features.Candidates.LanguageLevel.dto.responses.LanguageLevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/language-levels")
@RequiredArgsConstructor
public class LanguageLevelController {

    private final LanguageLevelService service;

    @PostMapping
    public ResponseEntity<LanguageLevelResponse> create(
        @Valid @RequestBody CreateLanguageLevelRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<LanguageLevelResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LanguageLevelResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LanguageLevelResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateLanguageLevelRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

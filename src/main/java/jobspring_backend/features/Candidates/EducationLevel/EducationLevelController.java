package jobspring_backend.features.Candidates.EducationLevel;

import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.EducationLevel.dto.requests.CreateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.requests.UpdateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.responses.EducationLevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/education-levels")
@RequiredArgsConstructor
public class EducationLevelController {

    private final EducationLevelService educationLevelService;

    @PostMapping
    public ResponseEntity<EducationLevelResponse> create(
        @Valid @RequestBody CreateEducationLevelRequest request
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(educationLevelService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<EducationLevelResponse>> getAll() {
        return ResponseEntity.ok(educationLevelService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationLevelResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(educationLevelService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationLevelResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateEducationLevelRequest request
    ) {
        return ResponseEntity.ok(educationLevelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        educationLevelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

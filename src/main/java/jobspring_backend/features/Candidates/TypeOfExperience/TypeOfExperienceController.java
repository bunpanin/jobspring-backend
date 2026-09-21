package jobspring_backend.features.Candidates.TypeOfExperience;
import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.TypeOfExperience.dto.requests.CreateTypeOfExperienceRequest;
import jobspring_backend.features.Candidates.TypeOfExperience.dto.requests.UpdateTypeOfExperienceRequest;
import jobspring_backend.features.Candidates.TypeOfExperience.dto.responses.TypeOfExperienceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/type-of-experiences")
@RequiredArgsConstructor
public class TypeOfExperienceController {

    private final TypeOfExperienceService service;

    @PostMapping
    public ResponseEntity<TypeOfExperienceResponse> create(@Valid @RequestBody CreateTypeOfExperienceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<TypeOfExperienceResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeOfExperienceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeOfExperienceResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateTypeOfExperienceRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

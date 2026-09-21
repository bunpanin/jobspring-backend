package jobspring_backend.features.Candidates.JobLevel;
import jakarta.validation.Valid;
import jobspring_backend.features.Candidates.JobLevel.dto.request.CreateJobLevelRequest;
import jobspring_backend.features.Candidates.JobLevel.dto.request.UpdateJobLevelRequest;
import jobspring_backend.features.Candidates.JobLevel.dto.respone.JobLevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-levels")
@RequiredArgsConstructor
public class JobLevelController {

    private final JobLevelService jobLevelService;

    @PostMapping
    public ResponseEntity<JobLevelResponse> create(@Valid @RequestBody CreateJobLevelRequest request) {
        JobLevelResponse response = jobLevelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<JobLevelResponse>> getAll() {
        return ResponseEntity.ok(jobLevelService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobLevelResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobLevelService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobLevelResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateJobLevelRequest request) {
        return ResponseEntity.ok(jobLevelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobLevelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
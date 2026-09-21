package jobspring_backend.features.Candidates.Major;

import jobspring_backend.features.Candidates.Education.EducationService;
import jobspring_backend.features.Candidates.Major.dto.responses.MajorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/majors")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService service;

    @GetMapping
    public ResponseEntity<List<MajorResponse>> getAll() {
        return ResponseEntity.ok(service.getMajors());
    }
}

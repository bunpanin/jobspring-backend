package jobspring_backend.features.Candidate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/Candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService service;

}

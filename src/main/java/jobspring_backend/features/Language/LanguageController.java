package jobspring_backend.features.Language;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/Language")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService service;

}

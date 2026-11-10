package jobspring_backend.features.Candidates.ExtractCv;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")

public class ExtractCvController {
    private final ExtractCvService extractCvService;

    public ExtractCvController(ExtractCvService pdfExtractService) {
        this.extractCvService = pdfExtractService;
    }

    @PostMapping(
            value = "/extract-edf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> extractPdf(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> data = extractCvService.extractPdf(file);

            response.put("success", true);
            response.put("message", "PDF extracted successfully");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to read PDF file");

            return ResponseEntity.status(
                    HttpStatus.UNPROCESSABLE_ENTITY
            ).body(response);
        }
    }
}

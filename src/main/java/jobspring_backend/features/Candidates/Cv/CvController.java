package jobspring_backend.features.Candidates.Cv;

import jobspring_backend.features.Candidates.Cv.dto.responses.CvResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
public class CvController {

    private final CvService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CvResponse> upload(
        @RequestParam("candidateId") Long candidateId,
        @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.upload(candidateId, file));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<CvResponse>> getByCandidateId(
        @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(service.getByCandidateId(candidateId));
    }

    @GetMapping("/{cvId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long cvId) {
        CvDownload download = service.download(cvId);
        ContentDisposition disposition = ContentDisposition
            .attachment()
            .filename(download.originalFileName(), StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(download.contentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .body(download.resource());
    }

    @GetMapping(value = "/{viewToken}/view", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> view(@PathVariable String viewToken) {
        CvDownload download = service.view(viewToken);
        ContentDisposition disposition = ContentDisposition
            .inline()
            .filename(download.originalFileName(), StandardCharsets.UTF_8)
            .build();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .body(download.resource());
    }

    @PatchMapping("/{cvId}/primary")
    public ResponseEntity<CvResponse> setPrimary(@PathVariable Long cvId) {
        return ResponseEntity.ok(service.setPrimary(cvId));
    }

    @DeleteMapping("/{cvId}")
    public ResponseEntity<Void> delete(@PathVariable Long cvId) {
        service.delete(cvId);
        return ResponseEntity.noContent().build();
    }
}

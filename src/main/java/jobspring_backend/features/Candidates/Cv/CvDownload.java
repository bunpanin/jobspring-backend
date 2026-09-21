package jobspring_backend.features.Candidates.Cv;

import org.springframework.core.io.Resource;

public record CvDownload(
    String originalFileName,
    String contentType,
    Resource resource
) {
}

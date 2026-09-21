package jobspring_backend.features.Cv;

import jobspring_backend.features.Candidates.Cv.CvController;
import jobspring_backend.features.Candidates.Cv.CvDownload;
import jobspring_backend.features.Candidates.Cv.CvService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CvControllerTest {

    @Test
    void viewReturnsThePdfWithInlineContentDisposition() {
        CvService service = mock(CvService.class);
        CvController controller = new CvController(service);
        Resource resource = new ByteArrayResource("pdf-content".getBytes());
        when(service.view("secure-view-token")).thenReturn(new CvDownload(
            "Dara-CV.pdf",
            "application/pdf",
            resource
        ));

        ResponseEntity<Resource> response = controller.view("secure-view-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).startsWith("inline"));
        assertSame(resource, response.getBody());
    }
}

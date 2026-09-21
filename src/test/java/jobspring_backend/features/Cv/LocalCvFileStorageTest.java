package jobspring_backend.features.Cv;

import jobspring_backend.features.Candidates.Cv.LocalCvFileStorage;
import jobspring_backend.features.Candidates.Cv.StoredCvFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalCvFileStorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void storeUsesAGeneratedNameAndLoadReturnsTheOriginalBytes() throws Exception {
        LocalCvFileStorage storage = new LocalCvFileStorage(temporaryDirectory.toString());
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "../../Dara CV.pdf",
            "application/pdf",
            "pdf-content".getBytes()
        );

        StoredCvFile stored = storage.store(file);
        Resource loaded = storage.load(stored.storedFileName());

        assertTrue(stored.storedFileName().endsWith(".pdf"));
        assertFalse(stored.storedFileName().contains("Dara"));
        assertFalse(stored.storedFileName().contains(".."));
        assertEquals("pdf-content", new String(loaded.getContentAsByteArray()));
    }

    @Test
    void loadRejectsAPathOutsideTheStorageDirectory() {
        LocalCvFileStorage storage = new LocalCvFileStorage(temporaryDirectory.toString());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> storage.load("../secret.pdf")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid CV file path", exception.getReason());
    }
}

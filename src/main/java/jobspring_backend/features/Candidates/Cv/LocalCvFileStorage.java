package jobspring_backend.features.Candidates.Cv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalCvFileStorage implements CvFileStorage {

    private final Path storageDirectory;

    public LocalCvFileStorage(
        @Value("${app.cv.storage-directory:uploads/cvs}") String storageDirectory
    ) {
        this.storageDirectory = Path.of(storageDirectory).toAbsolutePath().normalize();
    }

    @Override
    public StoredCvFile store(MultipartFile file) {
        String extension = extensionOf(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + "." + extension;
        Path destination = resolveSafely(storedFileName);

        try {
            Files.createDirectories(storageDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StoredCvFile(storedFileName);
        } catch (IOException exception) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not store CV file",
                exception
            );
        }
    }

    @Override
    public Resource load(String storedFileName) {
        Path file = resolveSafely(storedFileName);
        if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CV file not found");
        }

        try {
            return new UrlResource(file.toUri());
        } catch (MalformedURLException exception) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not read CV file",
                exception
            );
        }
    }

    private Path resolveSafely(String storedFileName) {
        Path resolved = storageDirectory.resolve(storedFileName).normalize();
        if (!resolved.startsWith(storageDirectory)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CV file path");
        }
        return resolved;
    }

    private String extensionOf(String fileName) {
        int separator = fileName == null ? -1 : fileName.lastIndexOf('.');
        return separator < 0 ? "" : fileName.substring(separator + 1).trim().toLowerCase();
    }
}

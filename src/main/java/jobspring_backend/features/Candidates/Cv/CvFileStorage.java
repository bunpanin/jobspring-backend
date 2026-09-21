package jobspring_backend.features.Candidates.Cv;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface CvFileStorage {

    StoredCvFile store(MultipartFile file);

    Resource load(String storedFileName);
}

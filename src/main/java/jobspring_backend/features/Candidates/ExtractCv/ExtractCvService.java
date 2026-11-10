package jobspring_backend.features.Candidates.ExtractCv;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface ExtractCvService {
    Map<String, Object> extractPdf(MultipartFile file) throws IOException;
}

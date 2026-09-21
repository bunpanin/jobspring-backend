package jobspring_backend.features.Candidates.Cv;

import jobspring_backend.features.Candidates.Cv.dto.responses.CvResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CvService {

    CvResponse upload(Long candidateId, MultipartFile file);

    List<CvResponse> getByCandidateId(Long candidateId);

    CvResponse setPrimary(Long cvId);

    void delete(Long cvId);

    CvDownload download(Long cvId);

    CvDownload view(String viewToken);
}

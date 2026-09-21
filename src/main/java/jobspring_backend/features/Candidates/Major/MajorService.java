package jobspring_backend.features.Candidates.Major;

import jobspring_backend.features.Candidates.Major.dto.responses.MajorResponse;

import java.util.List;

public interface MajorService {
    List<MajorResponse> getMajors();
}

package jobspring_backend.features.Candidates.Major;
import jobspring_backend.features.Candidates.Major.dto.responses.MajorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorServiceImpl implements MajorService {

    private final MajorRepository majorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MajorResponse> getMajors() {
        return majorRepository
                .findAllByIsDeletedFalseOrderByNameAsc()
                .stream()
                .map(major -> new MajorResponse(major.getMajorId(), major.getName()))
                .toList();
    }
}

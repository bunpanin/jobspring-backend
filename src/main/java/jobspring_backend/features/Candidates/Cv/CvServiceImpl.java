package jobspring_backend.features.Candidates.Cv;

import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.Cv.dto.responses.CvResponse;
import jobspring_backend.features.Candidates.Cv.entity.Cv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CvServiceImpl implements CvService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private final CvRepository cvRepository;
    private final CandidateRepository candidateRepository;
    private final CvFileStorage fileStorage;

    @Override
    public CvResponse upload(Long candidateId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CV file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "CV file must not exceed 10 MB"
            );
        }
        validateFileType(file);
        Candidate candidate = findCandidate(candidateId);
        boolean isPrimary = !cvRepository
            .existsByCandidate_CandidateIdAndIsDeletedFalseAndIsPrimaryTrue(candidateId);
        StoredCvFile storedFile = fileStorage.store(file);
        Cv cv = Cv.builder()
            .candidate(candidate)
            .originalFileName(file.getOriginalFilename().trim())
            .storedFileName(storedFile.storedFileName())
            .view(generateViewToken())
            .contentType(file.getContentType())
            .isPrimary(isPrimary)
            .isDeleted(false)
            .build();

        return mapToResponse(cvRepository.save(cv));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvResponse> getByCandidateId(Long candidateId) {
        findCandidate(candidateId);
        return cvRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public CvResponse setPrimary(Long cvId) {
        Cv selected = findCv(cvId);
        List<Cv> activeCvs = cvRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(
                selected.getCandidate().getCandidateId()
            );
        activeCvs.forEach(cv -> cv.setPrimary(false));
        selected.setPrimary(true);
        return mapToResponse(selected);
    }

    @Override
    public void delete(Long cvId) {
        Cv cv = findCv(cvId);
        List<Cv> activeCvs = cvRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(
                cv.getCandidate().getCandidateId()
            );
        boolean wasPrimary = cv.isPrimary();
        cv.setDeleted(true);
        cv.setPrimary(false);

        if (wasPrimary) {
            activeCvs.stream()
                .filter(activeCv -> !activeCv.getCvId().equals(cvId))
                .findFirst()
                .ifPresent(replacement -> replacement.setPrimary(true));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CvDownload download(Long cvId) {
        return toDownload(findCv(cvId));
    }

    @Override
    @Transactional(readOnly = true)
    public CvDownload view(String viewToken) {
        Cv cv = cvRepository
            .findByViewTokenAndIsDeletedFalse(viewToken)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "CV not found"
            ));
        return toDownload(cv);
    }

    private void validateFileType(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String extension = extensionOf(originalFileName);
        if (!"pdf".equals(extension) || !"application/pdf".equals(file.getContentType())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only PDF files are allowed"
            );
        }
    }

    private String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        int separator = fileName.lastIndexOf('.');
        return separator < 0 ? "" : fileName.substring(separator + 1).trim().toLowerCase();
    }

    private String generateViewToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Candidate findCandidate(Long candidateId) {
        return candidateRepository
            .findByCandidateIdAndIsDeletedFalse(candidateId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Candidate not found with ID: " + candidateId
            ));
    }

    private Cv findCv(Long cvId) {
        return cvRepository
            .findByCvIdAndIsDeletedFalse(cvId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "CV not found with ID: " + cvId
            ));
    }

    private CvDownload toDownload(Cv cv) {
        return new CvDownload(
            cv.getOriginalFileName(),
            cv.getContentType(),
            fileStorage.load(cv.getStoredFileName())
        );
    }

    private CvResponse mapToResponse(Cv cv) {
        return new CvResponse(
            cv.getCvId(),
            cv.getCandidate().getCandidateId(),
            cv.getOriginalFileName(),
            cv.getContentType(),
            cv.isPrimary(),
            cv.getCreatedDate(),
            "/api/v1/cvs/" + cv.getView() + "/view"
        );
    }
}

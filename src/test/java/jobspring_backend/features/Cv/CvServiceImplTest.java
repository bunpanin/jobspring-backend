package jobspring_backend.features.Cv;

import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.Cv.CvFileStorage;
import jobspring_backend.features.Candidates.Cv.CvDownload;
import jobspring_backend.features.Candidates.Cv.CvRepository;
import jobspring_backend.features.Candidates.Cv.CvServiceImpl;
import jobspring_backend.features.Candidates.Cv.StoredCvFile;
import jobspring_backend.features.Candidates.Cv.dto.responses.CvResponse;
import jobspring_backend.features.Candidates.Cv.entity.Cv;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CvServiceImplTest {

    @Test
    void uploadStoresTheFirstValidCvAsPrimary() {
        CvRepository cvRepository = mock(CvRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        CvFileStorage fileStorage = mock(CvFileStorage.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            candidateRepository,
            fileStorage
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        MockMultipartFile file = new MockMultipartFile(
            "file",
            " Dara-Sok-CV.pdf ",
            "application/pdf",
            "pdf-content".getBytes()
        );

        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(candidate));
        when(cvRepository.existsByCandidate_CandidateIdAndIsDeletedFalseAndIsPrimaryTrue(7L))
            .thenReturn(false);
        when(fileStorage.store(file))
            .thenReturn(new StoredCvFile("a4f1.pdf"));
        when(cvRepository.save(any(Cv.class)))
            .thenAnswer(invocation -> {
                Cv cv = invocation.getArgument(0);
                cv.setCvId(21L);
                cv.setCreatedDate(LocalDate.of(2026, 9, 21));
                return cv;
            });

        CvResponse result = service.upload(7L, file);

        assertEquals(21L, result.cvId());
        assertEquals(7L, result.candidateId());
        assertEquals("Dara-Sok-CV.pdf", result.originalFileName());
        assertEquals("application/pdf", result.contentType());
        assertEquals(11L, result.fileSize());
        assertTrue(result.isPrimary());
        assertEquals(LocalDate.of(2026, 9, 21), result.createdDate());
        assertTrue(result.viewUrl().matches("/api/v1/cvs/[0-9a-f]{32}/view"));
    }

    @Test
    void uploadDoesNotMakeALaterCvPrimary() {
        CvRepository cvRepository = mock(CvRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        CvFileStorage fileStorage = mock(CvFileStorage.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            candidateRepository,
            fileStorage
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "second.pdf",
            "application/pdf",
            "second-cv".getBytes()
        );
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(candidate));
        when(cvRepository.existsByCandidate_CandidateIdAndIsDeletedFalseAndIsPrimaryTrue(7L))
            .thenReturn(true);
        when(fileStorage.store(file)).thenReturn(new StoredCvFile("second.pdf"));
        when(cvRepository.save(any(Cv.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CvResponse result = service.upload(7L, file);

        assertFalse(result.isPrimary());
    }

    @Test
    void uploadRejectsAnUnsupportedFileType() {
        CvRepository cvRepository = mock(CvRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        CvFileStorage fileStorage = mock(CvFileStorage.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            candidateRepository,
            fileStorage
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "malware.exe",
            "application/octet-stream",
            "not-a-cv".getBytes()
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.upload(7L, file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Only PDF files are allowed", exception.getReason());
    }

    @Test
    void uploadRejectsADocFileEvenWhenItsContentTypeIsValid() {
        CvServiceImpl service = new CvServiceImpl(
            mock(CvRepository.class),
            mock(CandidateRepository.class),
            mock(CvFileStorage.class)
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "Dara-CV.doc",
            "application/msword",
            "doc-content".getBytes()
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.upload(7L, file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Only PDF files are allowed", exception.getReason());
    }

    @Test
    void uploadRejectsAnEmptyFile() {
        CvServiceImpl service = new CvServiceImpl(
            mock(CvRepository.class),
            mock(CandidateRepository.class),
            mock(CvFileStorage.class)
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "empty.pdf",
            "application/pdf",
            new byte[0]
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.upload(7L, file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CV file is required", exception.getReason());
    }

    @Test
    void uploadRejectsAFileLargerThanTenMegabytes() {
        CvServiceImpl service = new CvServiceImpl(
            mock(CvRepository.class),
            mock(CandidateRepository.class),
            mock(CvFileStorage.class)
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "large.pdf",
            "application/pdf",
            new byte[(10 * 1024 * 1024) + 1]
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.upload(7L, file)
        );

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exception.getStatusCode());
        assertEquals("CV file must not exceed 10 MB", exception.getReason());
    }

    @Test
    void getByCandidateIdReturnsOnlyActiveCvsNewestFirst() {
        CvRepository cvRepository = mock(CvRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            candidateRepository,
            mock(CvFileStorage.class)
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(candidate));
        when(cvRepository.findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(7L))
            .thenReturn(List.of(cv(22L, candidate, "new.pdf", true), cv(21L, candidate, "old.pdf", false)));

        List<CvResponse> result = service.getByCandidateId(7L);

        assertEquals(2, result.size());
        assertEquals(22L, result.get(0).cvId());
        assertEquals("new.pdf", result.get(0).originalFileName());
        assertEquals(21L, result.get(1).cvId());
    }

    @Test
    void setPrimaryUnsetsTheCandidatesPreviousPrimaryCv() {
        CvRepository cvRepository = mock(CvRepository.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            mock(CandidateRepository.class),
            mock(CvFileStorage.class)
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        Cv previousPrimary = cv(21L, candidate, "old.pdf", true);
        Cv selected = cv(22L, candidate, "new.pdf", false);
        when(cvRepository.findByCvIdAndIsDeletedFalse(22L)).thenReturn(Optional.of(selected));
        when(cvRepository.findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(7L))
            .thenReturn(List.of(selected, previousPrimary));

        CvResponse result = service.setPrimary(22L);

        assertTrue(result.isPrimary());
        assertTrue(selected.isPrimary());
        assertFalse(previousPrimary.isPrimary());
    }

    @Test
    void deleteSoftDeletesThePrimaryCvAndPromotesTheNewestRemainingCv() {
        CvRepository cvRepository = mock(CvRepository.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            mock(CandidateRepository.class),
            mock(CvFileStorage.class)
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        Cv primary = cv(22L, candidate, "primary.pdf", true);
        Cv replacement = cv(21L, candidate, "replacement.pdf", false);
        when(cvRepository.findByCvIdAndIsDeletedFalse(22L)).thenReturn(Optional.of(primary));
        when(cvRepository.findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByCreatedDateDescCvIdDesc(7L))
            .thenReturn(List.of(primary, replacement));

        service.delete(22L);

        assertTrue(primary.isDeleted());
        assertFalse(primary.isPrimary());
        assertTrue(replacement.isPrimary());
    }

    @Test
    void downloadReturnsTheStoredResourceAndOriginalMetadata() throws Exception {
        CvRepository cvRepository = mock(CvRepository.class);
        CvFileStorage fileStorage = mock(CvFileStorage.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            mock(CandidateRepository.class),
            fileStorage
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        Cv cv = cv(22L, candidate, "Dara-CV.pdf", true);
        ByteArrayResource resource = new ByteArrayResource("pdf-content".getBytes());
        when(cvRepository.findByCvIdAndIsDeletedFalse(22L)).thenReturn(Optional.of(cv));
        when(fileStorage.load("22.pdf")).thenReturn(resource);

        CvDownload result = service.download(22L);

        assertEquals("Dara-CV.pdf", result.originalFileName());
        assertEquals("application/pdf", result.contentType());
        assertEquals("pdf-content", new String(result.resource().getContentAsByteArray()));
    }

    @Test
    void viewFindsAnActiveCvByItsSecureToken() throws Exception {
        CvRepository cvRepository = mock(CvRepository.class);
        CvFileStorage fileStorage = mock(CvFileStorage.class);
        CvServiceImpl service = new CvServiceImpl(
            cvRepository,
            mock(CandidateRepository.class),
            fileStorage
        );
        Candidate candidate = Candidate.builder().candidateId(7L).build();
        Cv cv = cv(22L, candidate, "Dara-CV.pdf", true);
        ByteArrayResource resource = new ByteArrayResource("pdf-content".getBytes());
        when(cvRepository.findByViewTokenAndIsDeletedFalse("secure-view-token"))
            .thenReturn(Optional.of(cv));
        when(fileStorage.load("22.pdf")).thenReturn(resource);

        CvDownload result = service.view("secure-view-token");

        assertEquals("Dara-CV.pdf", result.originalFileName());
        assertEquals("application/pdf", result.contentType());
        assertEquals("pdf-content", new String(result.resource().getContentAsByteArray()));
    }

    private Cv cv(Long id, Candidate candidate, String originalFileName, boolean isPrimary) {
        return Cv.builder()
            .cvId(id)
            .candidate(candidate)
            .originalFileName(originalFileName)
            .storedFileName(id + ".pdf")
            .viewToken("token" + id)
            .contentType("application/pdf")
            .fileSize(100L)
            .isPrimary(isPrimary)
            .createdDate(LocalDate.of(2026, 9, 21))
            .isDeleted(false)
            .build();
    }
}

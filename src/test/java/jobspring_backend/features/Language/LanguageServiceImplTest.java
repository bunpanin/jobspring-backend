package jobspring_backend.features.Language;

import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.Language.CandidateLanguageRepository;
import jobspring_backend.features.Candidates.Language.LanguageRepository;
import jobspring_backend.features.Candidates.Language.LanguageServiceImpl;
import jobspring_backend.features.Candidates.Language.dto.requests.CreateCandidateLanguageRequest;
import jobspring_backend.features.Candidates.Language.dto.requests.UpdateCandidateLanguageRequest;
import jobspring_backend.features.Candidates.Language.dto.responses.CandidateLanguageResponse;
import jobspring_backend.features.Candidates.Language.entity.CandidateLanguage;
import jobspring_backend.features.Candidates.Language.entity.Language;
import jobspring_backend.features.Candidates.LanguageLevel.LanguageLevelRepository;
import jobspring_backend.features.Candidates.LanguageLevel.entity.LanguageLevel;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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

class LanguageServiceImplTest {

    @Test
    void createAssignsAnExistingLanguageAndLevelToTheCandidate() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.languageLevelRepository.findByLanguageLevelIdAndIsDeletedFalse(4L))
            .thenReturn(Optional.of(fixture.fluent));
        when(fixture.languageRepository.findByLanguageNameIgnoreCaseAndIsDeletedFalse("English"))
            .thenReturn(Optional.of(fixture.english));
        when(fixture.candidateLanguageRepository
            .findByCandidate_CandidateIdAndLanguage_LanguageId(7L, 11L))
            .thenReturn(Optional.empty());
        when(fixture.candidateLanguageRepository.save(any(CandidateLanguage.class)))
            .thenAnswer(invocation -> {
                CandidateLanguage assignment = invocation.getArgument(0);
                assignment.setId(21L);
                return assignment;
            });

        CandidateLanguageResponse result = fixture.service.create(
            new CreateCandidateLanguageRequest(7L, " English ", 4L)
        );

        assertEquals(7L, result.candidateId());
        assertEquals("English", result.languageName());
        assertEquals("Fluent", result.languageLevelName());
    }

    @Test
    void createRejectsAnActiveDuplicateAssignment() {
        Fixture fixture = new Fixture();
        CandidateLanguage existing = fixture.assignment(21L, fixture.english, fixture.fluent, false);
        fixture.stubCreateDependencies();
        when(fixture.candidateLanguageRepository
            .findByCandidate_CandidateIdAndLanguage_LanguageId(7L, 11L))
            .thenReturn(Optional.of(existing));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateCandidateLanguageRequest(7L, "English", 4L))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Candidate already has this language", exception.getReason());
    }

    @Test
    void createReactivatesADeletedAssignmentAndUpdatesItsLevel() {
        Fixture fixture = new Fixture();
        LanguageLevel advanced = fixture.level(5L, "Advanced");
        CandidateLanguage deleted = fixture.assignment(21L, fixture.english, fixture.fluent, true);
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.languageLevelRepository.findByLanguageLevelIdAndIsDeletedFalse(5L))
            .thenReturn(Optional.of(advanced));
        when(fixture.languageRepository.findByLanguageNameIgnoreCaseAndIsDeletedFalse("English"))
            .thenReturn(Optional.of(fixture.english));
        when(fixture.candidateLanguageRepository
            .findByCandidate_CandidateIdAndLanguage_LanguageId(7L, 11L))
            .thenReturn(Optional.of(deleted));

        CandidateLanguageResponse result = fixture.service.create(
            new CreateCandidateLanguageRequest(7L, "English", 5L)
        );

        assertFalse(deleted.getIsDeleted());
        assertEquals(advanced, deleted.getLanguageLevel());
        assertEquals("Advanced", result.languageLevelName());
    }

    @Test
    void createMultipleReturnsEveryCreatedAssignment() {
        Fixture fixture = new Fixture();
        Language french = fixture.language(12L, "French");
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.languageLevelRepository.findByLanguageLevelIdAndIsDeletedFalse(4L))
            .thenReturn(Optional.of(fixture.fluent));
        when(fixture.languageRepository.findByLanguageNameIgnoreCaseAndIsDeletedFalse("English"))
            .thenReturn(Optional.of(fixture.english));
        when(fixture.languageRepository.findByLanguageNameIgnoreCaseAndIsDeletedFalse("French"))
            .thenReturn(Optional.of(french));
        when(fixture.candidateLanguageRepository
            .findByCandidate_CandidateIdAndLanguage_LanguageId(any(), any()))
            .thenReturn(Optional.empty());
        when(fixture.candidateLanguageRepository.save(any(CandidateLanguage.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<CandidateLanguageResponse> result = fixture.service.createMultiple(List.of(
            new CreateCandidateLanguageRequest(7L, "English", 4L),
            new CreateCandidateLanguageRequest(7L, "French", 4L)
        ));

        assertEquals(2, result.size());
        assertEquals("English", result.get(0).languageName());
        assertEquals("French", result.get(1).languageName());
    }

    @Test
    void createMultipleRejectsAnEmptyRequest() {
        Fixture fixture = new Fixture();

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.createMultiple(List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Languages are required", exception.getReason());
    }

    @Test
    void getByCandidateIdReturnsActiveLanguageAssignments() {
        Fixture fixture = new Fixture();
        CandidateLanguage assignment = fixture.assignment(21L, fixture.english, fixture.fluent, false);
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.candidateLanguageRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseOrderByLanguage_LanguageNameAsc(7L))
            .thenReturn(List.of(assignment));

        List<CandidateLanguageResponse> result = fixture.service.getByCandidateId(7L);

        assertEquals(1, result.size());
        assertEquals(7L, result.getFirst().candidateId());
        assertEquals("English", result.getFirst().languageName());
        assertEquals("Fluent", result.getFirst().languageLevelName());
    }

    @Test
    void updateReassignsTheCandidateToAnotherSharedLanguageAndLevel() {
        Fixture fixture = new Fixture();
        Language french = fixture.language(12L, "French");
        LanguageLevel advanced = fixture.level(5L, "Advanced");
        CandidateLanguage assignment = fixture.assignment(21L, fixture.english, fixture.fluent, false);
        when(fixture.candidateLanguageRepository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndLanguage_LanguageIdAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseAndIsDeletedFalse(7L, 11L))
            .thenReturn(Optional.of(assignment));
        when(fixture.languageRepository.findByLanguageNameIgnoreCaseAndIsDeletedFalse("French"))
            .thenReturn(Optional.of(french));
        when(fixture.candidateLanguageRepository
            .existsByCandidate_CandidateIdAndLanguage_LanguageIdAndIsDeletedFalse(7L, 12L))
            .thenReturn(false);
        when(fixture.languageLevelRepository.findByLanguageLevelIdAndIsDeletedFalse(5L))
            .thenReturn(Optional.of(advanced));

        CandidateLanguageResponse result = fixture.service.update(
            7L,
            11L,
            new UpdateCandidateLanguageRequest(" French ", 5L)
        );

        assertEquals(french, assignment.getLanguage());
        assertEquals(advanced, assignment.getLanguageLevel());
        assertEquals("French", result.languageName());
        assertEquals("Advanced", result.languageLevelName());
    }

    @Test
    void deleteSoftDeletesTheCandidateLanguageAssignment() {
        Fixture fixture = new Fixture();
        CandidateLanguage assignment = fixture.assignment(21L, fixture.english, fixture.fluent, false);
        when(fixture.candidateLanguageRepository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndLanguage_LanguageIdAndLanguage_IsDeletedFalseAndLanguageLevel_IsDeletedFalseAndIsDeletedFalse(7L, 11L))
            .thenReturn(Optional.of(assignment));

        fixture.service.delete(7L, 11L);

        assertTrue(assignment.getIsDeleted());
    }

    private static class Fixture {
        private final CandidateLanguageRepository candidateLanguageRepository = mock(CandidateLanguageRepository.class);
        private final CandidateRepository candidateRepository = mock(CandidateRepository.class);
        private final LanguageRepository languageRepository = mock(LanguageRepository.class);
        private final LanguageLevelRepository languageLevelRepository = mock(LanguageLevelRepository.class);
        private final LanguageServiceImpl service = new LanguageServiceImpl(
            candidateLanguageRepository,
            candidateRepository,
            languageRepository,
            languageLevelRepository
        );
        private final Candidate candidate = Candidate.builder().candidateId(7L).build();
        private final Language english = language(11L, "English");
        private final LanguageLevel fluent = level(4L, "Fluent");

        private void stubCreateDependencies() {
            when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
                .thenReturn(Optional.of(candidate));
            when(languageLevelRepository.findByLanguageLevelIdAndIsDeletedFalse(4L))
                .thenReturn(Optional.of(fluent));
            when(languageRepository.findByLanguageNameIgnoreCaseAndIsDeletedFalse("English"))
                .thenReturn(Optional.of(english));
        }

        private CandidateLanguage assignment(
            Long id,
            Language language,
            LanguageLevel level,
            boolean isDeleted
        ) {
            return CandidateLanguage.builder()
                .id(id)
                .candidate(candidate)
                .language(language)
                .languageLevel(level)
                .isDeleted(isDeleted)
                .build();
        }

        private Language language(Long id, String name) {
            return Language.builder()
                .languageId(id)
                .languageName(name)
                .createdBy(7L)
                .createdDate(LocalDate.of(2026, 9, 19))
                .isDeleted(false)
                .build();
        }

        private LanguageLevel level(Long id, String name) {
            return LanguageLevel.builder()
                .languageLevelId(id)
                .languageLevelName(name)
                .createdBy(3L)
                .createdDate(LocalDate.of(2026, 9, 19))
                .isDeleted(false)
                .build();
        }
    }
}

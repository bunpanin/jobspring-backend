package jobspring_backend.features.LanguageLevel;

import jobspring_backend.features.LanguageLevel.dto.requests.CreateLanguageLevelRequest;
import jobspring_backend.features.LanguageLevel.dto.requests.UpdateLanguageLevelRequest;
import jobspring_backend.features.LanguageLevel.dto.responses.LanguageLevelResponse;
import jobspring_backend.features.LanguageLevel.entity.LanguageLevel;
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

class LanguageLevelServiceImplTest {

    @Test
    void createTrimsAndCreatesAnActiveLanguageLevel() {
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findByLanguageLevelNameIgnoreCase("Intermediate"))
            .thenReturn(Optional.empty());
        when(repository.save(any(LanguageLevel.class))).thenAnswer(invocation -> {
            LanguageLevel level = invocation.getArgument(0);
            level.setLanguageLevelId(1L);
            level.setCreatedDate(LocalDate.of(2026, 9, 19));
            return level;
        });
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        LanguageLevelResponse result = service.create(
            new CreateLanguageLevelRequest(" Intermediate ", 7L)
        );

        assertEquals(1L, result.languageLevelId());
        assertEquals("Intermediate", result.languageLevelName());
        assertEquals(7L, result.createdBy());
        assertEquals(LocalDate.of(2026, 9, 19), result.createdDate());
        assertFalse(result.isDeleted());
    }

    @Test
    void createRejectsAnActiveDuplicateIgnoringCase() {
        LanguageLevel existing = level(1L, "Intermediate", false);
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findByLanguageLevelNameIgnoreCase("INTERMEDIATE"))
            .thenReturn(Optional.of(existing));
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.create(new CreateLanguageLevelRequest("INTERMEDIATE", 7L))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Language level already exists", exception.getReason());
    }

    @Test
    void createReactivatesADeletedLanguageLevel() {
        LanguageLevel deleted = level(4L, "Fluent", true);
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findByLanguageLevelNameIgnoreCase("Fluent"))
            .thenReturn(Optional.of(deleted));
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        LanguageLevelResponse result = service.create(
            new CreateLanguageLevelRequest(" Fluent ", 9L)
        );

        assertEquals(4L, result.languageLevelId());
        assertFalse(result.isDeleted());
        assertEquals(9L, result.createdBy());
        assertFalse(deleted.getIsDeleted());
    }

    @Test
    void getAllReturnsActiveLanguageLevels() {
        List<LanguageLevel> levels = List.of(
            level(1L, "Beginner", false),
            level(2L, "Intermediate", false)
        );
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findAllByIsDeletedFalseOrderByLanguageLevelNameAsc())
            .thenReturn(levels);
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        List<LanguageLevelResponse> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("Beginner", result.get(0).languageLevelName());
        assertEquals("Intermediate", result.get(1).languageLevelName());
    }

    @Test
    void updateChangesTheNameOfAnActiveLanguageLevel() {
        LanguageLevel existing = level(2L, "Intermediate", false);
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findByLanguageLevelIdAndIsDeletedFalse(2L))
            .thenReturn(Optional.of(existing));
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        LanguageLevelResponse result = service.update(
            2L,
            new UpdateLanguageLevelRequest(" Upper Intermediate ")
        );

        assertEquals("Upper Intermediate", result.languageLevelName());
        assertEquals("Upper Intermediate", existing.getLanguageLevelName());
    }

    @Test
    void deleteSoftDeletesAnActiveLanguageLevel() {
        LanguageLevel existing = level(3L, "Advanced", false);
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findByLanguageLevelIdAndIsDeletedFalse(3L))
            .thenReturn(Optional.of(existing));
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        service.delete(3L);

        assertTrue(existing.getIsDeleted());
    }

    @Test
    void getByIdReturnsNotFoundForAMissingOrDeletedLanguageLevel() {
        LanguageLevelRepository repository = mock(LanguageLevelRepository.class);
        when(repository.findByLanguageLevelIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());
        LanguageLevelServiceImpl service = new LanguageLevelServiceImpl(repository);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.getById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Language level not found with ID: 99", exception.getReason());
    }

    private LanguageLevel level(Long id, String name, boolean isDeleted) {
        return LanguageLevel.builder()
            .languageLevelId(id)
            .languageLevelName(name)
            .createdBy(3L)
            .createdDate(LocalDate.of(2026, 1, 1))
            .isDeleted(isDeleted)
            .build();
    }
}

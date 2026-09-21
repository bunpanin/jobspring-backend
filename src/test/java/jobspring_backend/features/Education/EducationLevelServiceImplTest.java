package jobspring_backend.features.Education;

import jobspring_backend.features.Candidates.EducationLevel.EducationLevelRepository;
import jobspring_backend.features.Candidates.EducationLevel.EducationLevelServiceImpl;
import jobspring_backend.features.Candidates.EducationLevel.dto.requests.CreateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.requests.UpdateEducationLevelRequest;
import jobspring_backend.features.Candidates.EducationLevel.dto.responses.EducationLevelResponse;
import jobspring_backend.features.Candidates.EducationLevel.entity.EducationLevel;
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

class EducationLevelServiceImplTest {

    @Test
    void createTrimsAndCreatesAnActiveEducationLevel() {
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByNameIgnoreCase("Bachelor")).thenReturn(Optional.empty());
        when(repository.save(any(EducationLevel.class))).thenAnswer(invocation -> {
            EducationLevel level = invocation.getArgument(0);
            level.setEducationLevelId(3L);
            level.setCreatedDate(LocalDate.of(2026, 9, 20));
            return level;
        });
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        EducationLevelResponse result = service.create(
            new CreateEducationLevelRequest(" Bachelor ", "admin")
        );

        assertEquals(3L, result.educationLevelId());
        assertEquals("Bachelor", result.name());
        assertEquals("admin", result.createdBy());
        assertFalse(result.isDeleted());
    }

    @Test
    void createRejectsAnActiveDuplicateIgnoringCase() {
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByNameIgnoreCase("BACHELOR"))
            .thenReturn(Optional.of(level(3L, "Bachelor", false)));
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.create(new CreateEducationLevelRequest("BACHELOR", "admin"))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Education level already exists", exception.getReason());
    }

    @Test
    void createReactivatesADeletedEducationLevel() {
        EducationLevel deleted = level(4L, "Master", true);
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByNameIgnoreCase("Master")).thenReturn(Optional.of(deleted));
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        EducationLevelResponse result = service.create(
            new CreateEducationLevelRequest(" Master ", "admin-2")
        );

        assertEquals(4L, result.educationLevelId());
        assertEquals("admin-2", result.createdBy());
        assertFalse(deleted.isDeleted());
    }

    @Test
    void getAllReturnsActiveEducationLevelsAlphabetically() {
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findAllByIsDeletedFalseOrderByNameAsc()).thenReturn(List.of(
            level(2L, "Associate", false),
            level(3L, "Bachelor", false)
        ));
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        List<EducationLevelResponse> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("Associate", result.get(0).name());
        assertEquals("Bachelor", result.get(1).name());
    }

    @Test
    void updateChangesTheNameOfAnActiveEducationLevel() {
        EducationLevel existing = level(3L, "Bachelor", false);
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByEducationLevelIdAndIsDeletedFalse(3L))
            .thenReturn(Optional.of(existing));
        when(repository.existsByNameIgnoreCaseAndEducationLevelIdNotAndIsDeletedFalse(
            "Bachelor Degree", 3L
        )).thenReturn(false);
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        EducationLevelResponse result = service.update(
            3L,
            new UpdateEducationLevelRequest(" Bachelor Degree ")
        );

        assertEquals("Bachelor Degree", result.name());
        assertEquals("Bachelor Degree", existing.getName());
    }

    @Test
    void updateRejectsAnotherActiveLevelWithTheSameName() {
        EducationLevel existing = level(3L, "Bachelor", false);
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByEducationLevelIdAndIsDeletedFalse(3L))
            .thenReturn(Optional.of(existing));
        when(repository.existsByNameIgnoreCaseAndEducationLevelIdNotAndIsDeletedFalse(
            "Master", 3L
        )).thenReturn(true);
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.update(3L, new UpdateEducationLevelRequest("Master"))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Education level already exists", exception.getReason());
        assertEquals("Bachelor", existing.getName());
    }

    @Test
    void deleteSoftDeletesAnActiveEducationLevel() {
        EducationLevel existing = level(5L, "Doctoral", false);
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByEducationLevelIdAndIsDeletedFalse(5L))
            .thenReturn(Optional.of(existing));
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        service.delete(5L);

        assertTrue(existing.isDeleted());
    }

    @Test
    void getByIdReturnsNotFoundForAMissingOrDeletedEducationLevel() {
        EducationLevelRepository repository = mock(EducationLevelRepository.class);
        when(repository.findByEducationLevelIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());
        EducationLevelServiceImpl service = new EducationLevelServiceImpl(repository);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.getById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Education level not found with ID: 99", exception.getReason());
    }

    private EducationLevel level(Long id, String name, boolean isDeleted) {
        return EducationLevel.builder()
            .educationLevelId(id)
            .name(name)
            .createdBy("admin")
            .createdDate(LocalDate.of(2026, 1, 1))
            .isDeleted(isDeleted)
            .build();
    }
}

package jobspring_backend.features.Reference;

import jobspring_backend.features.Candidate.CandidateRepository;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.Reference.dto.requests.CreateReferenceRequest;
import jobspring_backend.features.Reference.dto.requests.UpdateReferenceRequest;
import jobspring_backend.features.Reference.dto.responses.ReferenceResponse;
import jobspring_backend.features.Reference.entity.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReferenceServiceImplTest {

    @Test
    void createStoresATrimmedReferenceForAnActiveCandidate() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        when(fixture.referenceRepository.save(any(Reference.class)))
            .thenAnswer(invocation -> {
                Reference reference = invocation.getArgument(0);
                reference.setReferenceId(31L);
                reference.setCreatedDate(LocalDate.of(2026, 9, 19));
                return reference;
            });

        ReferenceResponse result = fixture.service.create(new CreateReferenceRequest(
            7L,
            " Dara Sok ",
            " Engineering Manager ",
            " Acme Co. ",
            null,
            " dara@example.com ",
            "candidate-7"
        ));

        assertEquals(31L, result.referenceId());
        assertEquals(7L, result.candidateId());
        assertEquals("Dara Sok", result.fullName());
        assertEquals("Engineering Manager", result.position());
        assertEquals("Acme Co.", result.companyName());
        assertEquals("dara@example.com", result.email());
        assertEquals("candidate-7", result.createdBy());
        assertEquals(LocalDate.of(2026, 9, 19), result.createdDate());
    }

    @Test
    void createRejectsAReferenceWithoutEmailOrPhoneNumber() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateReferenceRequest(
                7L,
                "Dara Sok",
                "Engineering Manager",
                "Acme Co.",
                "   ",
                null,
                "candidate-7"
            ))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email or phone number is required", exception.getReason());
    }

    @Test
    void createRejectsAnInvalidEmailAddress() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateReferenceRequest(
                7L,
                "Dara Sok",
                "Engineering Manager",
                "Acme Co.",
                null,
                "not-an-email",
                "candidate-7"
            ))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email must be valid", exception.getReason());
    }

    @Test
    void createRejectsAMissingOrDeletedCandidate() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateReferenceRequest(
                99L,
                "Dara Sok",
                null,
                null,
                "+85512345678",
                null,
                "candidate-99"
            ))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Candidate not found with ID: 99", exception.getReason());
    }

    @Test
    void createMultipleReturnsEverySavedReference() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        when(fixture.referenceRepository.saveAll(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<ReferenceResponse> result = fixture.service.createMultiple(List.of(
            new CreateReferenceRequest(
                7L, "Dara Sok", "Manager", "Acme", null,
                "dara@example.com", "candidate-7"
            ),
            new CreateReferenceRequest(
                7L, "Sophy Lim", "Team Lead", "Beta", "+85598765432",
                null, "candidate-7"
            )
        ));

        assertEquals(2, result.size());
        assertEquals("Dara Sok", result.get(0).fullName());
        assertEquals("Sophy Lim", result.get(1).fullName());
    }

    @Test
    void createMultipleRejectsAnEmptyRequest() {
        Fixture fixture = new Fixture();

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.createMultiple(List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("References are required", exception.getReason());
    }

    @Test
    void getAllReturnsActiveReferencesNewestFirst() {
        Fixture fixture = new Fixture();
        when(fixture.referenceRepository.findAllByIsDeletedFalseOrderByReferenceIdDesc())
            .thenReturn(List.of(
                fixture.reference(32L, "Sophy Lim"),
                fixture.reference(31L, "Dara Sok")
            ));

        List<ReferenceResponse> result = fixture.service.getAll();

        assertEquals(2, result.size());
        assertEquals(32L, result.get(0).referenceId());
        assertEquals(31L, result.get(1).referenceId());
    }

    @Test
    void getByCandidateIdReturnsOnlyThatCandidatesActiveReferences() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        when(fixture.referenceRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByReferenceIdDesc(7L))
            .thenReturn(List.of(fixture.reference(31L, "Dara Sok")));

        List<ReferenceResponse> result = fixture.service.getByCandidateId(7L);

        assertEquals(1, result.size());
        assertEquals(7L, result.getFirst().candidateId());
        assertEquals("Dara Sok", result.getFirst().fullName());
    }

    @Test
    void getByIdReturnsNotFoundForAMissingOrDeletedReference() {
        Fixture fixture = new Fixture();
        when(fixture.referenceRepository.findByReferenceIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.getById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Reference not found with ID: 99", exception.getReason());
    }

    @Test
    void updateChangesOnlyProvidedReferenceFields() {
        Fixture fixture = new Fixture();
        Reference existing = fixture.reference(31L, "Dara Sok");
        when(fixture.referenceRepository.findByReferenceIdAndIsDeletedFalse(31L))
            .thenReturn(Optional.of(existing));

        ReferenceResponse result = fixture.service.update(
            31L,
            new UpdateReferenceRequest(
                " Dara S. ",
                " Director of Engineering ",
                null,
                " +85512345678 ",
                null
            )
        );

        assertEquals("Dara S.", result.fullName());
        assertEquals("Director of Engineering", result.position());
        assertEquals("Acme Co.", result.companyName());
        assertEquals("+85512345678", result.phoneNumber());
        assertEquals("dara@example.com", result.email());
    }

    @Test
    void updateRejectsRemovingTheOnlyContactMethod() {
        Fixture fixture = new Fixture();
        Reference existing = fixture.reference(31L, "Dara Sok");
        when(fixture.referenceRepository.findByReferenceIdAndIsDeletedFalse(31L))
            .thenReturn(Optional.of(existing));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.update(
                31L,
                new UpdateReferenceRequest(null, null, null, null, "   ")
            )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email or phone number is required", exception.getReason());
        assertEquals("dara@example.com", existing.getEmail());
    }

    @Test
    void updateRejectsABlankFullName() {
        Fixture fixture = new Fixture();
        Reference existing = fixture.reference(31L, "Dara Sok");
        when(fixture.referenceRepository.findByReferenceIdAndIsDeletedFalse(31L))
            .thenReturn(Optional.of(existing));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.update(
                31L,
                new UpdateReferenceRequest("   ", null, null, null, null)
            )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Reference full name is required", exception.getReason());
        assertEquals("Dara Sok", existing.getFullName());
    }

    @Test
    void deleteSoftDeletesAnActiveReference() {
        Fixture fixture = new Fixture();
        Reference existing = fixture.reference(31L, "Dara Sok");
        when(fixture.referenceRepository.findByReferenceIdAndIsDeletedFalse(31L))
            .thenReturn(Optional.of(existing));

        fixture.service.delete(31L);

        assertTrue(existing.isDeleted());
    }

    private static class Fixture {
        private final ReferenceRepository referenceRepository = mock(ReferenceRepository.class);
        private final CandidateRepository candidateRepository = mock(CandidateRepository.class);
        private final ReferenceServiceImpl service = new ReferenceServiceImpl(
            referenceRepository,
            candidateRepository
        );
        private final Candidate candidate = Candidate.builder()
            .candidateId(7L)
            .build();

        private void stubActiveCandidate() {
            when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
                .thenReturn(Optional.of(candidate));
        }

        private Reference reference(Long id, String fullName) {
            return Reference.builder()
                .referenceId(id)
                .candidate(candidate)
                .fullName(fullName)
                .position("Engineering Manager")
                .companyName("Acme Co.")
                .phoneNumber(null)
                .email("dara@example.com")
                .createdBy("candidate-7")
                .createdDate(LocalDate.of(2026, 9, 19))
                .isDeleted(false)
                .build();
        }
    }
}

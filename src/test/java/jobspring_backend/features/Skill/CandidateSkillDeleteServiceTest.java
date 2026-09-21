package jobspring_backend.features.Skill;

import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.Skill.CandidateSkillRepository;
import jobspring_backend.features.Candidates.Skill.SkillRepository;
import jobspring_backend.features.Candidates.Skill.SkillServiceImpl;
import jobspring_backend.features.Candidates.Skill.dto.requests.CreateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.responses.SkillResponse;
import jobspring_backend.features.Candidates.Skill.entity.CandidateSkill;
import jobspring_backend.features.Candidates.Skill.entity.Skill;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateSkillDeleteServiceTest {

    @Test
    void deleteSkillSoftDeletesTheSharedSkillWithoutDeletingCandidateRelationships() {
        CandidateSkill candidateSkill = candidateSkill(false, "Advanced");
        Skill skill = candidateSkill.getSkill();
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(skillRepository.findBySkillIdAndIsDeletedFalse(11L))
            .thenReturn(Optional.of(skill));
        SkillServiceImpl service = service(
            mock(CandidateSkillRepository.class),
            mock(CandidateRepository.class),
            skillRepository
        );

        service.deleteSkill(11L);

        assertTrue(skill.getIsDeleted());
        assertFalse(candidateSkill.getIsDeleted());
    }

    @Test
    void deleteSkillReturnsNotFoundWhenTheSharedSkillIsAlreadyDeleted() {
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(skillRepository.findBySkillIdAndIsDeletedFalse(11L))
            .thenReturn(Optional.empty());
        SkillServiceImpl service = service(
            mock(CandidateSkillRepository.class),
            mock(CandidateRepository.class),
            skillRepository
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.deleteSkill(11L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Skill not found with ID: 11", exception.getReason());
    }

    @Test
    void deleteSoftDeletesOnlyTheCandidatesSkillRelationship() {
        CandidateSkill candidateSkill = candidateSkill(false, "Advanced");
        CandidateSkillRepository repository = repositoryReturningActive(candidateSkill);
        SkillServiceImpl service = service(repository, mock(CandidateRepository.class), mock(SkillRepository.class));

        service.delete(7L, 11L);

        assertTrue(candidateSkill.getIsDeleted());
        assertFalse(candidateSkill.getSkill().getIsDeleted());
    }

    @Test
    void deleteReturnsNotFoundWhenTheRelationshipIsAlreadyDeleted() {
        CandidateSkillRepository repository = repositoryReturningActive(null);
        SkillServiceImpl service = service(repository, mock(CandidateRepository.class), mock(SkillRepository.class));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.delete(7L, 11L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Candidate skill not found", exception.getReason());
    }

    @Test
    void createReactivatesADeletedCandidateSkillAndReplacesItsDescription() {
        CandidateSkill deletedCandidateSkill = candidateSkill(true, "Old description");
        Candidate candidate = deletedCandidateSkill.getCandidate();
        Skill skill = deletedCandidateSkill.getSkill();
        CandidateSkillRepository candidateSkillRepository = mock(CandidateSkillRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(candidate));
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Java"))
            .thenReturn(Optional.of(skill));
        when(candidateSkillRepository.findByCandidate_CandidateIdAndSkill_SkillId(7L, 11L))
            .thenReturn(Optional.of(deletedCandidateSkill));
        when(candidateSkillRepository.save(any(CandidateSkill.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        SkillServiceImpl service = service(candidateSkillRepository, candidateRepository, skillRepository);

        SkillResponse result = service.create(
            new CreateSkillRequest("Java", 7L, "New description")
        );

        assertFalse(deletedCandidateSkill.getIsDeleted());
        assertEquals("New description", deletedCandidateSkill.getDescription());
        assertEquals("New description", result.description());
        assertEquals(7L, result.createdBy());
    }

    private CandidateSkillRepository repositoryReturningActive(CandidateSkill candidateSkill) {
        CandidateSkillRepository repository = mock(CandidateSkillRepository.class);
        when(repository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndSkill_SkillIdAndSkill_IsDeletedFalseAndIsDeletedFalse(
                7L,
                11L
            ))
            .thenReturn(Optional.ofNullable(candidateSkill));
        return repository;
    }

    private SkillServiceImpl service(
        CandidateSkillRepository candidateSkillRepository,
        CandidateRepository candidateRepository,
        SkillRepository skillRepository
    ) {
        return new SkillServiceImpl(candidateSkillRepository, candidateRepository, skillRepository);
    }

    private CandidateSkill candidateSkill(boolean isDeleted, String description) {
        Candidate candidate = Candidate.builder()
            .candidateId(7L)
            .build();
        Skill skill = Skill.builder()
            .skillId(11L)
            .skillName("Java")
            .createdBy(3L)
            .createdDate(LocalDate.of(2026, 9, 18))
            .isDeleted(false)
            .build();
        return CandidateSkill.builder()
            .id(21L)
            .candidate(candidate)
            .skill(skill)
            .description(description)
            .isDeleted(isDeleted)
            .build();
    }
}

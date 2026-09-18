package jobspring_backend.features.Skill;

import jobspring_backend.features.Candidate.CandidateRepository;
import jobspring_backend.features.Candidate.entity.Candidate;
import jobspring_backend.features.Skill.dto.requests.UpdateSkillRequest;
import jobspring_backend.features.Skill.dto.responses.SkillResponse;
import jobspring_backend.features.Skill.entity.CandidateSkill;
import jobspring_backend.features.Skill.entity.Skill;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateSkillUpdateServiceTest {

    @Test
    void updateChangesOnlyTheCandidatesDescription() {
        CandidateSkill candidateSkill = candidateSkill(7L, skill(11L, "Java"), "Old description");
        CandidateSkillRepository candidateSkillRepository = repositoryReturning(candidateSkill);
        SkillServiceImpl service = service(candidateSkillRepository, mock(SkillRepository.class));

        SkillResponse result = service.update(
            7L,
            11L,
            new UpdateSkillRequest(null, "Updated description")
        );

        assertEquals("Java", result.skillName());
        assertEquals("Updated description", result.description());
        assertEquals(7L, result.createdBy());
        assertEquals("Updated description", candidateSkill.getDescription());
    }

    @Test
    void updateReassignsOnlyTheCandidateToAnExistingSkill() {
        CandidateSkill candidateSkill = candidateSkill(7L, skill(11L, "Java"), "Advanced");
        Skill python = skill(12L, "Python");
        CandidateSkillRepository candidateSkillRepository = repositoryReturning(candidateSkill);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Python"))
            .thenReturn(Optional.of(python));
        SkillServiceImpl service = service(candidateSkillRepository, skillRepository);

        SkillResponse result = service.update(
            7L,
            11L,
            new UpdateSkillRequest(" Python ", null)
        );

        assertEquals(12L, result.skillId());
        assertEquals("Python", result.skillName());
        assertEquals("Advanced", result.description());
        assertSame(python, candidateSkill.getSkill());
    }

    @Test
    void updateCreatesAndAssignsANewSkillWhenTheNameDoesNotExist() {
        CandidateSkill candidateSkill = candidateSkill(7L, skill(11L, "Java"), "Advanced");
        Skill kotlin = skill(13L, "Kotlin");
        CandidateSkillRepository candidateSkillRepository = repositoryReturning(candidateSkill);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Kotlin"))
            .thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenReturn(kotlin);
        SkillServiceImpl service = service(candidateSkillRepository, skillRepository);

        SkillResponse result = service.update(
            7L,
            11L,
            new UpdateSkillRequest("Kotlin", "Intermediate")
        );

        assertEquals(13L, result.skillId());
        assertEquals("Kotlin", result.skillName());
        assertEquals("Intermediate", result.description());
        assertSame(kotlin, candidateSkill.getSkill());
    }

    @Test
    void updateRejectsAReplacementSkillTheCandidateAlreadyHas() {
        CandidateSkill candidateSkill = candidateSkill(7L, skill(11L, "Java"), "Advanced");
        Skill python = skill(12L, "Python");
        CandidateSkillRepository candidateSkillRepository = repositoryReturning(candidateSkill);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Python"))
            .thenReturn(Optional.of(python));
        when(candidateSkillRepository
            .existsByCandidate_CandidateIdAndSkill_SkillIdAndIsDeletedFalse(7L, 12L))
            .thenReturn(true);
        SkillServiceImpl service = service(candidateSkillRepository, skillRepository);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.update(7L, 11L, new UpdateSkillRequest("Python", null))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Candidate already has this skill", exception.getReason());
        assertEquals("Java", candidateSkill.getSkill().getSkillName());
    }

    @Test
    void updateReturnsNotFoundWhenTheCandidateDoesNotOwnTheSkill() {
        CandidateSkillRepository candidateSkillRepository = repositoryReturning(null);
        SkillServiceImpl service = service(candidateSkillRepository, mock(SkillRepository.class));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.update(7L, 11L, new UpdateSkillRequest("Python", null))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Candidate skill not found", exception.getReason());
    }

    @Test
    void updateRejectsABlankSkillName() {
        CandidateSkill candidateSkill = candidateSkill(7L, skill(11L, "Java"), "Advanced");
        SkillServiceImpl service = service(
            repositoryReturning(candidateSkill),
            mock(SkillRepository.class)
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.update(7L, 11L, new UpdateSkillRequest("   ", null))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Skill name is required", exception.getReason());
    }

    private SkillServiceImpl service(
        CandidateSkillRepository candidateSkillRepository,
        SkillRepository skillRepository
    ) {
        return new SkillServiceImpl(
            candidateSkillRepository,
            mock(CandidateRepository.class),
            skillRepository
        );
    }

    private CandidateSkillRepository repositoryReturning(CandidateSkill candidateSkill) {
        CandidateSkillRepository repository = mock(CandidateSkillRepository.class);
        when(repository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndSkill_SkillIdAndSkill_IsDeletedFalseAndIsDeletedFalse(
                7L,
                11L
            ))
            .thenReturn(Optional.ofNullable(candidateSkill));
        return repository;
    }

    private CandidateSkill candidateSkill(Long candidateId, Skill skill, String description) {
        Candidate candidate = Candidate.builder()
            .candidateId(candidateId)
            .build();
        return CandidateSkill.builder()
            .id(21L)
            .candidate(candidate)
            .skill(skill)
            .description(description)
            .build();
    }

    private Skill skill(Long skillId, String skillName) {
        return Skill.builder()
            .skillId(skillId)
            .skillName(skillName)
            .createdBy(3L)
            .createdDate(LocalDate.of(2026, 9, 18))
            .isDeleted(false)
            .build();
    }
}

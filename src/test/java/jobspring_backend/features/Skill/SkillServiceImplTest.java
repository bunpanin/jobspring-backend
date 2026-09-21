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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SkillServiceImplTest {

    @Test
    void createMultipleReturnsEveryCreatedCandidateSkill() {
        Long candidateId = 7L;
        Candidate candidate = Candidate.builder()
            .candidateId(candidateId)
            .build();
        Skill java = Skill.builder()
            .skillId(11L)
            .skillName("Java")
            .createdBy(candidateId)
            .createdDate(LocalDate.of(2026, 9, 18))
            .isDeleted(false)
            .build();
        Skill springBoot = Skill.builder()
            .skillId(12L)
            .skillName("Spring Boot")
            .createdBy(candidateId)
            .createdDate(LocalDate.of(2026, 9, 18))
            .isDeleted(false)
            .build();
        List<CreateSkillRequest> requests = List.of(
            new CreateSkillRequest("Java", candidateId, "Advanced"),
            new CreateSkillRequest("Spring Boot", candidateId, "Intermediate")
        );

        CandidateSkillRepository candidateSkillRepository = mock(CandidateSkillRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(candidateId))
            .thenReturn(Optional.of(candidate));
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Java"))
            .thenReturn(Optional.of(java));
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Spring Boot"))
            .thenReturn(Optional.of(springBoot));
        when(candidateSkillRepository.save(any(CandidateSkill.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SkillServiceImpl service = new SkillServiceImpl(
            candidateSkillRepository,
            candidateRepository,
            skillRepository
        );

        List<SkillResponse> result = service.createMultiple(requests);

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).skillName());
        assertEquals("Advanced", result.get(0).description());
        assertEquals("Spring Boot", result.get(1).skillName());
        assertEquals("Intermediate", result.get(1).description());
    }

    @Test
    void createMultipleRejectsAnEmptyRequest() {
        SkillServiceImpl service = new SkillServiceImpl(
            mock(CandidateSkillRepository.class),
            mock(CandidateRepository.class),
            mock(SkillRepository.class)
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.createMultiple(List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Skills are required", exception.getReason());
    }

    @Test
    void createMultipleRejectsTheBatchWhenItContainsADuplicateSkill() {
        Long candidateId = 7L;
        Candidate candidate = Candidate.builder()
            .candidateId(candidateId)
            .build();
        Skill java = Skill.builder()
            .skillId(11L)
            .skillName("Java")
            .createdBy(candidateId)
            .createdDate(LocalDate.of(2026, 9, 18))
            .isDeleted(false)
            .build();
        List<CreateSkillRequest> requests = List.of(
            new CreateSkillRequest("Java", candidateId, "Advanced"),
            new CreateSkillRequest("Java", candidateId, "Expert")
        );
        CandidateSkill existingCandidateSkill = CandidateSkill.builder()
            .id(21L)
            .candidate(candidate)
            .skill(java)
            .description("Advanced")
            .isDeleted(false)
            .build();

        CandidateSkillRepository candidateSkillRepository = mock(CandidateSkillRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(candidateId))
            .thenReturn(Optional.of(candidate));
        when(skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse("Java"))
            .thenReturn(Optional.of(java));
        when(candidateSkillRepository
            .findByCandidate_CandidateIdAndSkill_SkillId(candidateId, java.getSkillId()))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(existingCandidateSkill));
        when(candidateSkillRepository.save(any(CandidateSkill.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        SkillServiceImpl service = new SkillServiceImpl(
            candidateSkillRepository,
            candidateRepository,
            skillRepository
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.createMultiple(requests)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Candidate already has this skill", exception.getReason());
    }

    @Test
    void getByCandidateIdReturnsTheCandidatesSkillsWithDescriptions() {
        Long candidateId = 7L;
        Candidate candidate = Candidate.builder()
            .candidateId(candidateId)
            .build();
        Skill java = Skill.builder()
            .skillId(11L)
            .skillName("Java")
            .createdBy(3L)
            .createdDate(LocalDate.of(2026, 9, 18))
            .isDeleted(false)
            .build();
        CandidateSkill candidateSkill = CandidateSkill.builder()
            .id(21L)
            .candidate(candidate)
            .skill(java)
            .description("Spring Boot development")
            .build();

        CandidateSkillRepository candidateSkillRepository = mock(CandidateSkillRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(candidateId))
            .thenReturn(Optional.of(candidate));
        when(candidateSkillRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseAndSkill_IsDeletedFalseOrderBySkill_SkillNameAsc(candidateId))
            .thenReturn(List.of(candidateSkill));

        SkillServiceImpl service = new SkillServiceImpl(
            candidateSkillRepository,
            candidateRepository,
            skillRepository
        );

        List<SkillResponse> result = service.getByCandidateId(candidateId);

        assertEquals(1, result.size());
        assertEquals(11L, result.getFirst().skillId());
        assertEquals("Java", result.getFirst().skillName());
        assertEquals(7L, result.getFirst().createdBy());
        assertEquals("Spring Boot development", result.getFirst().description());
        assertEquals(LocalDate.of(2026, 9, 18), result.getFirst().createdDate());
    }

    @Test
    void getByCandidateIdReturnsNotFoundWhenCandidateDoesNotExist() {
        Long candidateId = 99L;
        CandidateSkillRepository candidateSkillRepository = mock(CandidateSkillRepository.class);
        CandidateRepository candidateRepository = mock(CandidateRepository.class);
        SkillRepository skillRepository = mock(SkillRepository.class);
        when(candidateRepository.findByCandidateIdAndIsDeletedFalse(candidateId))
            .thenReturn(Optional.empty());

        SkillServiceImpl service = new SkillServiceImpl(
            candidateSkillRepository,
            candidateRepository,
            skillRepository
        );

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> service.getByCandidateId(candidateId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Candidate not found", exception.getReason());
    }
}

package jobspring_backend.features.Achievement;

import jobspring_backend.features.Achievement.dto.requests.CreateAchievementRequest;
import jobspring_backend.features.Achievement.dto.requests.UpdateAchievementRequest;
import jobspring_backend.features.Achievement.dto.responses.AchievementResponse;
import jobspring_backend.features.Achievement.entity.Achievement;
import jobspring_backend.features.Candidate.CandidateRepository;
import jobspring_backend.features.Candidate.entity.Candidate;
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

class AchievementServiceImplTest {

    @Test
    void createStoresATrimmedAchievementForAnActiveCandidate() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.achievementRepository.save(any(Achievement.class)))
            .thenAnswer(invocation -> {
                Achievement achievement = invocation.getArgument(0);
                achievement.setAchievementId(21L);
                achievement.setCreatedDate(LocalDate.of(2026, 9, 19));
                return achievement;
            });

        AchievementResponse result = fixture.service.create(new CreateAchievementRequest(
            7L,
            " AWS Certified Developer ",
            LocalDate.of(2026, 8, 20),
            "Passed the associate certification",
            "candidate-7"
        ));

        assertEquals(21L, result.achievementId());
        assertEquals(7L, result.candidateId());
        assertEquals("AWS Certified Developer", result.title());
        assertEquals(LocalDate.of(2026, 8, 20), result.achievementDate());
        assertEquals("Passed the associate certification", result.description());
        assertEquals("candidate-7", result.createdBy());
        assertEquals(LocalDate.of(2026, 9, 19), result.createdDate());
    }

    @Test
    void createRejectsAMissingOrDeletedCandidate() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateAchievementRequest(
                99L,
                "Hackathon Winner",
                LocalDate.of(2026, 7, 1),
                null,
                "candidate-99"
            ))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Candidate not found with ID: 99", exception.getReason());
    }

    @Test
    void createMultipleReturnsEverySavedAchievement() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.achievementRepository.saveAll(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<AchievementResponse> result = fixture.service.createMultiple(List.of(
            new CreateAchievementRequest(
                7L,
                "Hackathon Winner",
                LocalDate.of(2026, 5, 1),
                null,
                "candidate-7"
            ),
            new CreateAchievementRequest(
                7L,
                "Top Student",
                LocalDate.of(2026, 6, 1),
                "Ranked first",
                "candidate-7"
            )
        ));

        assertEquals(2, result.size());
        assertEquals("Hackathon Winner", result.get(0).title());
        assertEquals("Top Student", result.get(1).title());
    }

    @Test
    void createMultipleRejectsAnEmptyRequest() {
        Fixture fixture = new Fixture();

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.createMultiple(List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Achievements are required", exception.getReason());
    }

    @Test
    void getAllReturnsActiveAchievementsNewestFirst() {
        Fixture fixture = new Fixture();
        when(fixture.achievementRepository.findAllByIsDeletedFalseOrderByAchievementIdDesc())
            .thenReturn(List.of(
                fixture.achievement(22L, "Top Student"),
                fixture.achievement(21L, "Hackathon Winner")
            ));

        List<AchievementResponse> result = fixture.service.getAll();

        assertEquals(2, result.size());
        assertEquals(22L, result.get(0).achievementId());
        assertEquals(21L, result.get(1).achievementId());
    }

    @Test
    void getByCandidateIdReturnsOnlyThatCandidatesActiveAchievements() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
            .thenReturn(Optional.of(fixture.candidate));
        when(fixture.achievementRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByAchievementIdDesc(7L))
            .thenReturn(List.of(fixture.achievement(21L, "Hackathon Winner")));

        List<AchievementResponse> result = fixture.service.getByCandidateId(7L);

        assertEquals(1, result.size());
        assertEquals(7L, result.getFirst().candidateId());
        assertEquals("Hackathon Winner", result.getFirst().title());
    }

    @Test
    void getByIdReturnsNotFoundForAMissingOrDeletedAchievement() {
        Fixture fixture = new Fixture();
        when(fixture.achievementRepository.findByAchievementIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.getById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Achievement not found with ID: 99", exception.getReason());
    }

    @Test
    void updateChangesOnlyProvidedAchievementFields() {
        Fixture fixture = new Fixture();
        Achievement existing = fixture.achievement(21L, "Hackathon Winner");
        when(fixture.achievementRepository.findByAchievementIdAndIsDeletedFalse(21L))
            .thenReturn(Optional.of(existing));

        AchievementResponse result = fixture.service.update(
            21L,
            new UpdateAchievementRequest(
                " Regional Hackathon Champion ",
                LocalDate.of(2026, 5, 2),
                "Won first place"
            )
        );

        assertEquals("Regional Hackathon Champion", result.title());
        assertEquals(LocalDate.of(2026, 5, 2), result.achievementDate());
        assertEquals("Won first place", result.description());
    }

    @Test
    void updateRejectsABlankTitle() {
        Fixture fixture = new Fixture();
        Achievement existing = fixture.achievement(21L, "Hackathon Winner");
        when(fixture.achievementRepository.findByAchievementIdAndIsDeletedFalse(21L))
            .thenReturn(Optional.of(existing));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.update(
                21L,
                new UpdateAchievementRequest("   ", null, null)
            )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Achievement title is required", exception.getReason());
        assertEquals("Hackathon Winner", existing.getTitle());
    }

    @Test
    void deleteSoftDeletesAnActiveAchievement() {
        Fixture fixture = new Fixture();
        Achievement existing = fixture.achievement(21L, "Hackathon Winner");
        when(fixture.achievementRepository.findByAchievementIdAndIsDeletedFalse(21L))
            .thenReturn(Optional.of(existing));

        fixture.service.delete(21L);

        assertTrue(existing.isDeleted());
    }

    private static class Fixture {
        private final AchievementRepository achievementRepository = mock(AchievementRepository.class);
        private final CandidateRepository candidateRepository = mock(CandidateRepository.class);
        private final AchievementServiceImpl service = new AchievementServiceImpl(
            achievementRepository,
            candidateRepository
        );
        private final Candidate candidate = Candidate.builder()
            .candidateId(7L)
            .build();

        private Achievement achievement(Long id, String title) {
            return Achievement.builder()
                .achievementId(id)
                .candidate(candidate)
                .title(title)
                .achievementDate(LocalDate.of(2026, 5, 1))
                .description("Description")
                .createdBy("candidate-7")
                .createdDate(LocalDate.of(2026, 5, 2))
                .isDeleted(false)
                .build();
        }
    }
}

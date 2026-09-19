package jobspring_backend.features.Achievement;

import jobspring_backend.features.Achievement.dto.requests.CreateAchievementRequest;
import jobspring_backend.features.Achievement.dto.requests.UpdateAchievementRequest;
import jobspring_backend.features.Achievement.dto.responses.AchievementResponse;
import jobspring_backend.features.Achievement.entity.Achievement;
import jobspring_backend.features.Candidate.CandidateRepository;
import jobspring_backend.features.Candidate.entity.Candidate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final CandidateRepository candidateRepository;

    @Override
    public AchievementResponse create(CreateAchievementRequest request) {
        Achievement achievement = buildAchievement(request);
        return mapToResponse(achievementRepository.save(achievement));
    }

    @Override
    public List<AchievementResponse> createMultiple(List<CreateAchievementRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Achievements are required"
            );
        }

        List<Achievement> achievements = requests.stream()
            .map(this::buildAchievement)
            .toList();

        return achievementRepository.saveAll(achievements)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponse> getAll() {
        return achievementRepository
            .findAllByIsDeletedFalseOrderByAchievementIdDesc()
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementResponse getById(Long id) {
        return mapToResponse(findAchievement(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponse> getByCandidateId(Long candidateId) {
        findCandidate(candidateId);
        return achievementRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByAchievementIdDesc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    public AchievementResponse update(Long id, UpdateAchievementRequest request) {
        Achievement achievement = findAchievement(id);

        if (request.title() != null) {
            String title = request.title().trim();
            if (title.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Achievement title is required"
                );
            }
            achievement.setTitle(title);
        }

        if (request.achievementDate() != null) {
            achievement.setAchievementDate(request.achievementDate());
        }

        if (request.description() != null) {
            achievement.setDescription(request.description().trim());
        }

        return mapToResponse(achievement);
    }

    @Override
    public void delete(Long id) {
        Achievement achievement = findAchievement(id);
        achievement.setDeleted(true);
    }

    private Achievement buildAchievement(CreateAchievementRequest request) {
        Candidate candidate = findCandidate(request.candidateId());
        return Achievement.builder()
            .candidate(candidate)
            .title(request.title().trim())
            .achievementDate(request.achievementDate())
            .description(trim(request.description()))
            .createdBy(trim(request.createdBy()))
            .isDeleted(false)
            .build();
    }

    private Achievement findAchievement(Long id) {
        return achievementRepository
            .findByAchievementIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Achievement not found with ID: " + id
            ));
    }

    private Candidate findCandidate(Long candidateId) {
        return candidateRepository
            .findByCandidateIdAndIsDeletedFalse(candidateId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Candidate not found with ID: " + candidateId
            ));
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private AchievementResponse mapToResponse(Achievement achievement) {
        return new AchievementResponse(
            achievement.getAchievementId(),
            achievement.getCandidate().getCandidateId(),
            achievement.getTitle(),
            achievement.getAchievementDate(),
            achievement.getDescription(),
            achievement.getCreatedBy(),
            achievement.getCreatedDate()
        );
    }
}

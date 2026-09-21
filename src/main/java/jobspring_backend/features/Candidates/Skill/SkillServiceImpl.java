package jobspring_backend.features.Candidates.Skill;
import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.Skill.dto.requests.CreateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.requests.UpdateSkillRequest;
import jobspring_backend.features.Candidates.Skill.dto.responses.SkillResponse;
import jobspring_backend.features.Candidates.Skill.entity.CandidateSkill;
import jobspring_backend.features.Candidates.Skill.entity.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SkillServiceImpl implements SkillService {

    private final CandidateSkillRepository candidateSkillRepository;
    private final CandidateRepository candidateRepository;
    private final SkillRepository skillRepository;


    @Override
    @Transactional
    public SkillResponse create(CreateSkillRequest request) {
        return createSkill(request);
    }

    @Override
    @Transactional
    public List<SkillResponse> createMultiple(List<CreateSkillRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Skills are required"
            );
        }

        return requests
            .stream()
            .map(this::createSkill)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getAll() {
        return skillRepository
            .findAllByIsDeletedFalseOrderBySkillNameAsc()
            .stream()
            .map(this::mapSkillToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getByCandidateId(Long candidateId) {
        candidateRepository
            .findByCandidateIdAndIsDeletedFalse(candidateId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidate not found"
                )
            );

        return candidateSkillRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseAndSkill_IsDeletedFalseOrderBySkill_SkillNameAsc(candidateId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse getById(Long id) {
        Skill skill = findSkill(id);
        return mapSkillToResponse(skill);
    }


    @Override
    @Transactional
    public SkillResponse update(Long candidateId, Long skillId, UpdateSkillRequest request) {
        CandidateSkill candidateSkill = candidateSkillRepository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndSkill_SkillIdAndSkill_IsDeletedFalseAndIsDeletedFalse(
                candidateId,
                skillId
            ).orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidate skill not found"
                )
            );

        if (request.skillName() != null) {
            String skillName = request.skillName().trim();
            if (skillName.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Skill name is required"
                );
            }

            if (!candidateSkill.getSkill().getSkillName().equalsIgnoreCase(skillName)) {
                Skill targetSkill = skillRepository
                    .findBySkillNameIgnoreCaseAndIsDeletedFalse(skillName)
                    .orElseGet(() -> skillRepository.save(
                        Skill.builder()
                            .skillName(skillName)
                            .createdBy(candidateId)
                            .build()
                    ));

                boolean alreadyExists = candidateSkillRepository
                    .existsByCandidate_CandidateIdAndSkill_SkillIdAndIsDeletedFalse(
                        candidateId,
                        targetSkill.getSkillId()
                    );
                if (alreadyExists) {
                    throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Candidate already has this skill"
                    );
                }

                candidateSkill.setSkill(targetSkill);
            }
        }

        if (request.description() != null) {
            candidateSkill.setDescription(request.description());
        }

        return mapToResponse(candidateSkill);
    }

    @Override
    @Transactional
    public void delete(Long candidateId, Long skillId) {
        CandidateSkill candidateSkill = candidateSkillRepository
            .findByCandidate_CandidateIdAndCandidate_IsDeletedFalseAndSkill_SkillIdAndSkill_IsDeletedFalseAndIsDeletedFalse(
                candidateId,
                skillId
            ).orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Candidate skill not found"
                )
            );

        candidateSkill.setIsDeleted(true);
    }

    @Override
    @Transactional
    public void deleteSkill(Long skillId) {
        Skill skill = findSkill(skillId);
        skill.setIsDeleted(true);
    }

    // Helper method

    private SkillResponse createSkill(CreateSkillRequest request) {
        Candidate candidate = candidateRepository
                .findByCandidateIdAndIsDeletedFalse(request.createdBy())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Candidate not found"
                        )
                );

        String skillName = request.skillName().trim();
        Skill skill = skillRepository.findBySkillNameIgnoreCaseAndIsDeletedFalse(skillName)
                .orElseGet(() -> {
                    Skill newSkill = Skill.builder()
                            .skillName(skillName)
                            .createdBy(request.createdBy())
                            .build();
                    return skillRepository.save(newSkill);
                });

        CandidateSkill existingCandidateSkill = candidateSkillRepository
                .findByCandidate_CandidateIdAndSkill_SkillId(request.createdBy(), skill.getSkillId())
                .orElse(null);

        if (existingCandidateSkill != null) {
            if (!Boolean.TRUE.equals(existingCandidateSkill.getIsDeleted())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Candidate already has this skill"
                );
            }

            existingCandidateSkill.setIsDeleted(false);
            existingCandidateSkill.setDescription(request.description());
            return mapToResponse(existingCandidateSkill);
        }

        CandidateSkill candidateSkill = CandidateSkill.builder()
                .candidate(candidate)
                .skill(skill)
                .description(request.description())
                .isDeleted(false)
                .build();

        CandidateSkill saved = candidateSkillRepository.save(candidateSkill);

        return new SkillResponse(
                skill.getSkillId(),
                skill.getSkillName(),
                request.createdBy(),
                saved.getDescription(), skill.getCreatedDate()
        );
    }

    private Skill findSkill(Long id) {
        return skillRepository
            .findBySkillIdAndIsDeletedFalse(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Skill not found with ID: " + id
                    )
            );
    }

    private SkillResponse mapSkillToResponse(Skill skill) {
        return new SkillResponse(
            skill.getSkillId(),
            skill.getSkillName(),
            skill.getCreatedBy(),
            null,
            skill.getCreatedDate()
        );
    }

    private SkillResponse mapToResponse(CandidateSkill candidateSkill) {
        Skill skill = candidateSkill.getSkill();
        return new SkillResponse(
            skill.getSkillId(),
            skill.getSkillName(),
            candidateSkill.getCandidate().getCandidateId(),
            candidateSkill.getDescription(),
            skill.getCreatedDate()
        );
    }



}

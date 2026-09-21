package jobspring_backend.features.Education;

import jobspring_backend.features.Candidates.Candidate.CandidateRepository;
import jobspring_backend.features.Candidates.Candidate.entity.Candidate;
import jobspring_backend.features.Candidates.EducationLevel.EducationLevelRepository;
import jobspring_backend.features.Candidates.Education.EducationRepository;
import jobspring_backend.features.Candidates.Education.EducationServiceImpl;
import jobspring_backend.features.Candidates.Major.MajorRepository;
import jobspring_backend.features.Candidates.Education.dto.requests.CreateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.requests.UpdateEducationRequest;
import jobspring_backend.features.Candidates.Education.dto.responses.EducationResponse;
import jobspring_backend.features.Candidates.Major.dto.responses.MajorResponse;
import jobspring_backend.features.Candidates.Education.entity.Education;
import jobspring_backend.features.Candidates.EducationLevel.entity.EducationLevel;
import jobspring_backend.features.Candidates.Major.entity.Major;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EducationServiceImplTest {

    @Test
    void createStoresEducationUsingAnExistingMajor() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        fixture.stubLevel(3L, fixture.bachelor);
        when(fixture.majorRepository.findByNameIgnoreCaseAndIsDeletedFalse("Computer Science"))
            .thenReturn(Optional.of(fixture.computerScience));
        when(fixture.educationRepository.save(any(Education.class)))
            .thenAnswer(invocation -> {
                Education education = invocation.getArgument(0);
                education.setEducationId(41L);
                education.setCreatedDate(LocalDate.of(2026, 9, 19));
                return education;
            });

        EducationResponse result = fixture.service.create(new CreateEducationRequest(
            7L,
            " Royal University of Phnom Penh ",
            3L,
            " Computer Science ",
            " Phnom Penh ",
            " Cambodia ",
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2026, 1, 1),
            false,
            true,
            "candidate-7"
        ));

        assertEquals(41L, result.educationId());
        assertEquals(7L, result.candidateId());
        assertEquals("Royal University of Phnom Penh", result.institution());
        assertEquals(3L, result.educationLevelId());
        assertEquals("Bachelor", result.educationLevelName());
        assertEquals(11L, result.majorId());
        assertEquals("Computer Science", result.majorName());
        assertEquals("Phnom Penh", result.city());
        assertEquals("Cambodia", result.country());
        assertTrue(result.isHidden());
    }

    @Test
    void createCreatesANewReusableMajorWhenItDoesNotExist() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        fixture.stubLevel(3L, fixture.bachelor);
        when(fixture.majorRepository.findByNameIgnoreCaseAndIsDeletedFalse("Data Science"))
            .thenReturn(Optional.empty());
        when(fixture.majorRepository.save(any(Major.class)))
            .thenAnswer(invocation -> {
                Major major = invocation.getArgument(0);
                major.setMajorId(12L);
                return major;
            });
        when(fixture.educationRepository.save(any(Education.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        EducationResponse result = fixture.service.create(fixture.request(
            "Data Science",
            LocalDate.of(2022, 1, 1),
            LocalDate.of(2026, 1, 1),
            false
        ));

        assertEquals(12L, result.majorId());
        assertEquals("Data Science", result.majorName());
    }

    @Test
    void createAllowsEducationWithoutAMajor() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        fixture.stubLevel(1L, fixture.highSchool);
        when(fixture.educationRepository.save(any(Education.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        EducationResponse result = fixture.service.create(new CreateEducationRequest(
            7L,
            "Hun Sen High School",
            1L,
            null,
            "Phnom Penh",
            "Cambodia",
            LocalDate.of(2018, 1, 1),
            LocalDate.of(2021, 1, 1),
            false,
            false,
            "candidate-7"
        ));

        assertNull(result.majorId());
        assertNull(result.majorName());
    }

    @Test
    void createCurrentEducationClearsTheEndDate() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        fixture.stubLevel(3L, fixture.bachelor);
        when(fixture.majorRepository.findByNameIgnoreCaseAndIsDeletedFalse("Computer Science"))
            .thenReturn(Optional.of(fixture.computerScience));
        when(fixture.educationRepository.save(any(Education.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        EducationResponse result = fixture.service.create(fixture.request(
            "Computer Science",
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2027, 1, 1),
            true
        ));

        assertTrue(result.isCurrent());
        assertNull(result.endDate());
    }

    @Test
    void createRejectsAnEndDateBeforeTheStartDate() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        fixture.stubLevel(3L, fixture.bachelor);
        when(fixture.majorRepository.findByNameIgnoreCaseAndIsDeletedFalse("Computer Science"))
            .thenReturn(Optional.of(fixture.computerScience));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(fixture.request(
                "Computer Science",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2025, 1, 1),
                false
            ))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("End date cannot be before start date", exception.getReason());
    }

    @Test
    void createRejectsAMissingOrDeletedCandidate() {
        Fixture fixture = new Fixture();
        when(fixture.candidateRepository.findByCandidateIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateEducationRequest(
                99L, "RUPP", 3L, null,
                null, null, null, null, false, false, "candidate-99"
            ))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Candidate not found with ID: 99", exception.getReason());
    }

    @Test
    void createRejectsAMissingOrDeletedEducationLevel() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        when(fixture.educationLevelRepository.findByEducationLevelIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.create(new CreateEducationRequest(
                7L, "RUPP", 99L, null,
                null, null, null, null, false, false, "candidate-7"
            ))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Education level not found with ID: 99", exception.getReason());
    }

    @Test
    void createMultipleReturnsEverySavedEducation() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        fixture.stubLevel(3L, fixture.bachelor);
        fixture.stubLevel(1L, fixture.highSchool);
        when(fixture.majorRepository.findByNameIgnoreCaseAndIsDeletedFalse("Computer Science"))
            .thenReturn(Optional.of(fixture.computerScience));
        when(fixture.educationRepository.saveAll(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<EducationResponse> result = fixture.service.createMultiple(List.of(
            fixture.request("Computer Science", LocalDate.of(2022, 1, 1), LocalDate.of(2026, 1, 1), false),
            new CreateEducationRequest(
                7L, "Hun Sen High School", 1L, null,
                "Phnom Penh", "Cambodia", LocalDate.of(2018, 1, 1),
                LocalDate.of(2021, 1, 1), false, false, "candidate-7"
            )
        ));

        assertEquals(2, result.size());
        assertEquals("Bachelor", result.get(0).educationLevelName());
        assertEquals("High School", result.get(1).educationLevelName());
    }

    @Test
    void createMultipleRejectsAnEmptyRequest() {
        Fixture fixture = new Fixture();

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.createMultiple(List.of())
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Educations are required", exception.getReason());
    }

    @Test
    void getAllReturnsActiveEducationsNewestFirst() {
        Fixture fixture = new Fixture();
        Education newer = fixture.education();
        newer.setEducationId(42L);
        when(fixture.educationRepository.findAllByIsDeletedFalseOrderByEducationIdDesc())
            .thenReturn(List.of(newer, fixture.education()));

        List<EducationResponse> result = fixture.service.getAll();

        assertEquals(2, result.size());
        assertEquals(42L, result.get(0).educationId());
        assertEquals(41L, result.get(1).educationId());
    }

    @Test
    void getByCandidateIdReturnsThatCandidatesActiveEducations() {
        Fixture fixture = new Fixture();
        fixture.stubActiveCandidate();
        when(fixture.educationRepository
            .findAllByCandidate_CandidateIdAndIsDeletedFalseOrderByEducationIdDesc(7L))
            .thenReturn(List.of(fixture.education()));

        List<EducationResponse> result = fixture.service.getByCandidateId(7L);

        assertEquals(1, result.size());
        assertEquals(7L, result.getFirst().candidateId());
        assertEquals("Royal University of Phnom Penh", result.getFirst().institution());
    }

    @Test
    void updateCanReassignMajorAndMarkEducationAsCurrentAndVisible() {
        Fixture fixture = new Fixture();
        Education education = fixture.education();
        Major dataScience = fixture.major(12L, "Data Science");
        when(fixture.educationRepository.findByEducationIdAndIsDeletedFalse(41L))
            .thenReturn(Optional.of(education));
        when(fixture.majorRepository.findByNameIgnoreCaseAndIsDeletedFalse("Data Science"))
            .thenReturn(Optional.of(dataScience));
        fixture.stubLevel(4L, fixture.master);

        EducationResponse result = fixture.service.update(
            41L,
            new UpdateEducationRequest(
                null,
                4L,
                " Data Science ",
                null,
                null,
                null,
                LocalDate.of(2028, 1, 1),
                true,
                false
            )
        );

        assertEquals(4L, result.educationLevelId());
        assertEquals("Master", result.educationLevelName());
        assertEquals("Data Science", result.majorName());
        assertTrue(result.isCurrent());
        assertNull(result.endDate());
        assertFalse(result.isHidden());
    }

    @Test
    void updateRejectsABlankInstitution() {
        Fixture fixture = new Fixture();
        Education education = fixture.education();
        when(fixture.educationRepository.findByEducationIdAndIsDeletedFalse(41L))
            .thenReturn(Optional.of(education));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.update(
                41L,
                new UpdateEducationRequest("   ", null, null, null, null, null, null, null, null)
            )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Institution is required", exception.getReason());
        assertEquals("Royal University of Phnom Penh", education.getInstitution());
    }

    @Test
    void getByIdReturnsNotFoundForAMissingOrDeletedEducation() {
        Fixture fixture = new Fixture();
        when(fixture.educationRepository.findByEducationIdAndIsDeletedFalse(99L))
            .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> fixture.service.getById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Education not found with ID: 99", exception.getReason());
    }

    @Test
    void deleteSoftDeletesAnActiveEducation() {
        Fixture fixture = new Fixture();
        Education education = fixture.education();
        when(fixture.educationRepository.findByEducationIdAndIsDeletedFalse(41L))
            .thenReturn(Optional.of(education));

        fixture.service.delete(41L);

        assertTrue(education.isDeleted());
    }

    @Test
    void getMajorsReturnsActiveMajorsAlphabetically() {
        Fixture fixture = new Fixture();
        when(fixture.majorRepository.findAllByIsDeletedFalseOrderByNameAsc())
            .thenReturn(List.of(
                fixture.computerScience,
                fixture.major(12L, "Data Science")
            ));

        List<MajorResponse> result = fixture.service.getMajors();

        assertEquals(2, result.size());
        assertEquals("Computer Science", result.get(0).name());
        assertEquals("Data Science", result.get(1).name());
    }

    private static class Fixture {
        private final EducationRepository educationRepository = mock(EducationRepository.class);
        private final CandidateRepository candidateRepository = mock(CandidateRepository.class);
        private final MajorRepository majorRepository = mock(MajorRepository.class);
        private final EducationLevelRepository educationLevelRepository = mock(EducationLevelRepository.class);
        private final EducationServiceImpl service = new EducationServiceImpl(
            educationRepository,
            candidateRepository,
            majorRepository,
            educationLevelRepository
        );
        private final Candidate candidate = Candidate.builder().candidateId(7L).build();
        private final Major computerScience = major(11L, "Computer Science");
        private final EducationLevel highSchool = level(1L, "High School");
        private final EducationLevel bachelor = level(3L, "Bachelor");
        private final EducationLevel master = level(4L, "Master");

        private void stubActiveCandidate() {
            when(candidateRepository.findByCandidateIdAndIsDeletedFalse(7L))
                .thenReturn(Optional.of(candidate));
        }

        private void stubLevel(Long id, EducationLevel level) {
            when(educationLevelRepository.findByEducationLevelIdAndIsDeletedFalse(id))
                .thenReturn(Optional.of(level));
        }

        private CreateEducationRequest request(
            String majorName,
            LocalDate startDate,
            LocalDate endDate,
            boolean isCurrent
        ) {
            return new CreateEducationRequest(
                7L,
                "Royal University of Phnom Penh",
                3L,
                majorName,
                "Phnom Penh",
                "Cambodia",
                startDate,
                endDate,
                isCurrent,
                false,
                "candidate-7"
            );
        }

        private Education education() {
            return Education.builder()
                .educationId(41L)
                .candidate(candidate)
                .institution("Royal University of Phnom Penh")
                .educationLevel(bachelor)
                .major(computerScience)
                .city("Phnom Penh")
                .country("Cambodia")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .isCurrent(false)
                .isHidden(true)
                .createdBy("candidate-7")
                .createdDate(LocalDate.of(2026, 9, 19))
                .isDeleted(false)
                .build();
        }

        private Major major(Long id, String name) {
            return Major.builder()
                .majorId(id)
                .name(name)
                .createdBy("candidate-7")
                .createdDate(LocalDate.of(2026, 9, 19))
                .isDeleted(false)
                .build();
        }

        private EducationLevel level(Long id, String name) {
            return EducationLevel.builder()
                .educationLevelId(id)
                .name(name)
                .createdBy("admin")
                .createdDate(LocalDate.of(2026, 9, 19))
                .isDeleted(false)
                .build();
        }
    }
}

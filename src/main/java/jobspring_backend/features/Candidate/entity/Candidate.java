package jobspring_backend.features.Candidate.entity;

import jakarta.persistence.*;
import jobspring_backend.features.JobLevel.entity.JobLevel;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(length = 20, nullable = true)
    private String gender;

    @Column(name = "contact_number", length = 20, nullable = true)
    private String contactNumber;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "job_title", length = 50, nullable = true)
    private String jobTitle;

    @ManyToOne
    @JoinColumn(name = "job_level_id")
    private JobLevel jobLevel;

    @Column(length = 50, nullable = true)
    private String industry;

    @Column(length = 100, nullable = true)
    private String address;

    @Column(name = "city", length = 50, nullable = true)
    private String city;

    @Column(length = 50, nullable = true)
    private String country;

    @Column(name = "github_username", length = 50, nullable = true)
    private String githubUsername;

    @Column(name = "linkedin_username", length = 50, nullable = true)
    private String linkedinUsername;

    @Column(length = 100, nullable = true)
    private String portfolio;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    private boolean isDeleted;

    private LocalDate createdAt;

    @PrePersist
    public  void  prePersist() {
        createdAt = LocalDate.now();
        isDeleted = false;
    }
}

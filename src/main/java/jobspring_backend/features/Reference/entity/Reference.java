package jobspring_backend.features.Reference.entity;

import jakarta.persistence.*;
import jobspring_backend.features.Candidate.entity.Candidate;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "candidate_references")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reference_id")
    private Long referenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @Column(name = "position", length = 150)
    private String position;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now();
        isDeleted = false;
    }
}

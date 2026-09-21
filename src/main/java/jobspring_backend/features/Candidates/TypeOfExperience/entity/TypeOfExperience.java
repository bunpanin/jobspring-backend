package jobspring_backend.features.Candidates.TypeOfExperience.entity;
import jakarta.persistence.*;
import jobspring_backend.features.Candidates.WorkExperience.entity.WorkExperience;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "type_of_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeOfExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_of_experience_id")
    private Long typeOfExperienceId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "typeOfExperience")
    private List<WorkExperience> workExperiences;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now();
        isDeleted = false;
    }
}

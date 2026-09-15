package jobspring_backend.domain.candidate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "educations")
@Setter
@Getter
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


}

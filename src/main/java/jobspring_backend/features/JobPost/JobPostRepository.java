package jobspring_backend.features.JobPost;
import jobspring_backend.domain.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    boolean existsByJobCode(String jobCode);

    boolean existsByJobCodeAndIdNot(String jobCode, UUID id);

    Optional<JobPost> findByIdAndCreatedByUserId(UUID id, String createdByUserId);

    List<JobPost> findAllByCreatedByUserIdOrderByCreatedAtDesc(
            String createdByUserId
    );
}
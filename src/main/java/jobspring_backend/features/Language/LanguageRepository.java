package jobspring_backend.features.Language;

import jobspring_backend.features.Language.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {

}

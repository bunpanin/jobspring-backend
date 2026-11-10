package jobspring_backend.features.Candidates.Ollama;
import tools.jackson.databind.JsonNode;

public interface OllamaService {
    JsonNode extractData(String extractedText);
}

package jobspring_backend.features.Candidates.ExtractCv;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.HashMap;

@Service
public class ExtractCvServiceImpl implements ExtractCvService {
    @Override
    public Map<String, Object> extractPdf(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please upload a PDF file"
            );
        }

        byte[] pdfBytes = file.getBytes();
        if (pdfBytes.length < 5 || !new String(pdfBytes, 0, 5, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-")) {
            throw new IllegalArgumentException("Invalid PDF file");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String extractedText = stripper.getText(document);

            Map<String, Object> result = new HashMap<>();

            result.put("fileName", file.getOriginalFilename());
            result.put("totalPages", document.getNumberOfPages());
            result.put("extractedText", extractedText.trim());
            result.put("requiresOcr", extractedText.isBlank());
            return result;
        }
    }
}
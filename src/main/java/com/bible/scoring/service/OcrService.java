package com.bible.scoring.service;

import com.bible.scoring.config.OcrProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    private final OcrProperties ocrProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String extractText(MultipartFile imageFile) {
        String apiKey = ocrProperties.getGoogle().getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("YOUR_")) {
            throw new RuntimeException("Google Cloud Vision API 키가 설정되지 않았습니다. application.yml의 ocr.google.api-key를 설정해주세요.");
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());

            Map<String, Object> requestBody = Map.of(
                "requests", List.of(Map.of(
                    "image", Map.of("content", base64Image),
                    "features", List.of(Map.of(
                        "type", "DOCUMENT_TEXT_DETECTION",
                        "maxResults", 1
                    ))
                ))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String url = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode responses = root.path("responses");
            if (responses.isArray() && responses.size() > 0) {
                JsonNode fullText = responses.get(0).path("fullTextAnnotation").path("text");
                if (!fullText.isMissingNode()) {
                    return fullText.asText("");
                }
            }
            return "";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("OCR 처리 실패: {}", e.getMessage());
            throw new RuntimeException("이미지 텍스트 추출에 실패했습니다: " + e.getMessage(), e);
        }
    }
}

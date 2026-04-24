package com.bible.scoring.controller;

import com.bible.scoring.dto.ImageScoringResultDto;
import com.bible.scoring.dto.ImageVerseInputDto;
import com.bible.scoring.service.ImageScoringService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ImageScoringApiController {

    private final ImageScoringService imageScoringService;
    private final ObjectMapper objectMapper;

    @PostMapping("/api/image-scoring/score")
    public ResponseEntity<?> score(
            @RequestParam String contestName,
            @RequestParam String participantName,
            @RequestParam(defaultValue = "") String bibleBook,
            @RequestParam(defaultValue = "") String bibleChapter,
            @RequestParam String versesJson,
            @RequestParam MultipartFile imageFile) {

        try {
            List<ImageVerseInputDto> verses = objectMapper.readValue(
                    versesJson, new TypeReference<>() {});

            ImageScoringResultDto result = imageScoringService.score(
                    contestName, participantName, bibleBook, bibleChapter, verses, imageFile);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/api/image-scoring/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Long id) {
        try {
            imageScoringService.softDelete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

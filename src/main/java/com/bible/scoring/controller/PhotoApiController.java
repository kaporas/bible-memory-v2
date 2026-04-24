package com.bible.scoring.controller;

import com.bible.scoring.entity.PhotoSubmission;
import com.bible.scoring.service.PhotoSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PhotoApiController {

    private final PhotoSubmissionService photoSubmissionService;

    @PostMapping("/api/photo/upload")
    public ResponseEntity<?> upload(
            @RequestParam String name,
            @RequestParam(required = false) String note,
            @RequestParam MultipartFile file) {
        try {
            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "이름은 필수입니다."));
            }
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "파일을 선택해주세요."));
            }
            PhotoSubmission submission = photoSubmissionService.save(name.trim(), note, file);
            return ResponseEntity.ok(Map.of("id", submission.getId(), "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/photo/{id}/update")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) MultipartFile file) {
        try {
            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "이름은 필수입니다."));
            }
            PhotoSubmission submission = photoSubmissionService.update(id, name.trim(), note, file);
            return ResponseEntity.ok(Map.of("id", submission.getId(), "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/photo/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        try {
            PhotoSubmission submission = photoSubmissionService.findById(id);
            String encodedName = URLEncoder.encode(submission.getFileName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(
                            submission.getFileType() != null ? submission.getFileType() : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedName)
                    .body(submission.getFileData());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

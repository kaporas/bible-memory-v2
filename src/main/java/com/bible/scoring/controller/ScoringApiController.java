package com.bible.scoring.controller;

import com.bible.scoring.dto.ScoringRequest;
import com.bible.scoring.dto.ScoringResultDto;
import com.bible.scoring.service.ScoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScoringApiController {

    private final ScoringService scoringService;

    @PostMapping("/score")
    public ResponseEntity<ScoringResultDto> score(@Valid @RequestBody ScoringRequest request) {
        ScoringResultDto result = scoringService.score(request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/scoring/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Long id) {
        try {
            scoringService.softDelete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

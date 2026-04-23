package com.bible.scoring.controller;

import com.bible.scoring.dto.ScoringRequest;
import com.bible.scoring.dto.ScoringResultDto;
import com.bible.scoring.service.ScoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

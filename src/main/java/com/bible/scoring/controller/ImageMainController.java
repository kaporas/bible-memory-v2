package com.bible.scoring.controller;

import com.bible.scoring.service.ImageScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ImageMainController {

    private final ImageScoringService imageScoringService;

    @GetMapping("/image-scoring")
    public String imageScoringPage() {
        return "image-scoring";
    }

    @GetMapping("/image-history")
    public String imageHistory(Model model) {
        model.addAttribute("sessions", imageScoringService.findAllSessions());
        return "image-history";
    }

    @GetMapping("/image-history/{id}")
    public String imageHistoryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("detail", imageScoringService.findSessionById(id));
        return "image-history-detail";
    }
}

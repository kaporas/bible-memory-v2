package com.bible.scoring.controller;

import com.bible.scoring.service.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ScoringService scoringService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("sessions", scoringService.findAllSessions());
        return "history";
    }

    @GetMapping("/history/{id}")
    public String historyDetail(@PathVariable Long id, Model model) {
        model.addAttribute("detail", scoringService.findSessionById(id));
        return "history-detail";
    }
}

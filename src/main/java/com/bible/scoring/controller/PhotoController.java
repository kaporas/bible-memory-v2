package com.bible.scoring.controller;

import com.bible.scoring.service.PhotoSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoSubmissionService photoSubmissionService;

    @GetMapping("/photo")
    public String photoUpload() {
        return "photo-upload";
    }

    @GetMapping("/photo/edit/{id}")
    public String photoEdit(@PathVariable Long id, Model model) {
        model.addAttribute("submission", photoSubmissionService.findDtoById(id));
        return "photo-upload";
    }

    @GetMapping("/photo/list")
    public String photoList(Model model) {
        model.addAttribute("submissions", photoSubmissionService.findAll());
        return "photo-list";
    }
}

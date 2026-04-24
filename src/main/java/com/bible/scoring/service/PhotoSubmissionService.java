package com.bible.scoring.service;

import com.bible.scoring.dto.PhotoSubmissionDto;
import com.bible.scoring.entity.PhotoSubmission;
import com.bible.scoring.repository.PhotoSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoSubmissionService {

    private final PhotoSubmissionRepository repository;

    @Transactional
    public PhotoSubmission save(String name, String note, MultipartFile file) throws IOException {
        PhotoSubmission submission = new PhotoSubmission();
        submission.setName(name);
        submission.setNote(note);
        submission.setFileName(file.getOriginalFilename());
        submission.setFileType(file.getContentType());
        submission.setFileSize(file.getSize());
        submission.setFileData(file.getBytes());
        return repository.save(submission);
    }

    @Transactional
    public PhotoSubmission update(Long id, String name, String note, MultipartFile file) throws IOException {
        PhotoSubmission submission = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("제출 기록을 찾을 수 없습니다: " + id));
        submission.setName(name);
        submission.setNote(note);
        if (file != null && !file.isEmpty()) {
            submission.setFileName(file.getOriginalFilename());
            submission.setFileType(file.getContentType());
            submission.setFileSize(file.getSize());
            submission.setFileData(file.getBytes());
        }
        return repository.save(submission);
    }

    @Transactional(readOnly = true)
    public List<PhotoSubmissionDto> findAll() {
        return repository.findAllDtos();
    }

    @Transactional(readOnly = true)
    public PhotoSubmission findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("제출 기록을 찾을 수 없습니다: " + id));
    }

    @Transactional(readOnly = true)
    public PhotoSubmissionDto findDtoById(Long id) {
        PhotoSubmission p = findById(id);
        return new PhotoSubmissionDto(p.getId(), p.getName(), p.getNote(),
                p.getFileName(), p.getFileType(), p.getFileSize(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}

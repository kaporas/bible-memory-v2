package com.bible.scoring.repository;

import com.bible.scoring.dto.PhotoSubmissionDto;
import com.bible.scoring.entity.PhotoSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotoSubmissionRepository extends JpaRepository<PhotoSubmission, Long> {

    // 파일 데이터(BLOB) 제외 목록 조회 - 대용량 파일 메모리 낭비 방지
    @Query("SELECT new com.bible.scoring.dto.PhotoSubmissionDto(" +
           "p.id, p.name, p.note, p.fileName, p.fileType, p.fileSize, p.createdAt, p.updatedAt) " +
           "FROM PhotoSubmission p WHERE p.deleted = false ORDER BY p.createdAt DESC")
    List<PhotoSubmissionDto> findAllDtos();
}

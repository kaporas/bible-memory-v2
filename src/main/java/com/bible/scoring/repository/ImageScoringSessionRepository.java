package com.bible.scoring.repository;

import com.bible.scoring.entity.ImageScoringSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageScoringSessionRepository extends JpaRepository<ImageScoringSession, Long> {

    List<ImageScoringSession> findAllByDeletedFalseOrderByCreatedAtDesc();

    @Query("SELECT s FROM ImageScoringSession s LEFT JOIN FETCH s.verseResults WHERE s.id = :id")
    Optional<ImageScoringSession> findByIdWithResults(@Param("id") Long id);
}

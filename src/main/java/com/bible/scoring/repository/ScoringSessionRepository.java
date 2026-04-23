package com.bible.scoring.repository;

import com.bible.scoring.entity.ScoringSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoringSessionRepository extends JpaRepository<ScoringSession, Long> {

    List<ScoringSession> findAllByOrderByCreatedAtDesc();

    @Query("SELECT s FROM ScoringSession s LEFT JOIN FETCH s.verseResults WHERE s.id = :id")
    Optional<ScoringSession> findByIdWithResults(@Param("id") Long id);
}

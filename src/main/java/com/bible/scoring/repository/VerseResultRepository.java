package com.bible.scoring.repository;

import com.bible.scoring.entity.VerseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerseResultRepository extends JpaRepository<VerseResult, Long> {

    List<VerseResult> findBySessionIdOrderByVerseOrderAsc(Long sessionId);
}

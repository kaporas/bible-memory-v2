package com.bible.scoring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "verse_results")
@Getter
@Setter
@NoArgsConstructor
public class VerseResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ScoringSession session;

    @Column(nullable = false)
    private Integer verseOrder;

    @Column(nullable = false, length = 20)
    private String verseRef;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String referenceText;

    @Column(columnDefinition = "TEXT")
    private String inputText;

    @Column(nullable = false)
    private Integer editDistance;

    @Column(nullable = false)
    private Boolean correct;

    @Column(nullable = false)
    private Integer refLength;
}

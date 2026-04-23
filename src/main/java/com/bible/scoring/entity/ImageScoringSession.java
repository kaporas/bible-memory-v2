package com.bible.scoring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "image_scoring_sessions")
@Getter
@Setter
@NoArgsConstructor
public class ImageScoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String contestName;

    @Column(nullable = false, length = 50)
    private String participantName;

    @Column(length = 50)
    private String bibleBook;

    @Column(length = 100)
    private String bibleChapter;

    @Column(nullable = false)
    private Double accuracy;

    @Column(nullable = false)
    private Boolean passed;

    @Column(nullable = false)
    private Integer totalVerses;

    @Column(nullable = false)
    private Integer correctVerses;

    @Column(nullable = false)
    private Integer totalChars;

    @Column(nullable = false)
    private Integer correctChars;

    @Column(nullable = false)
    private Integer wrongChars;

    @Column(length = 255)
    private String imageFilename;

    @Column(columnDefinition = "TEXT")
    private String ocrRawText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("verseOrder ASC")
    private List<ImageVerseResult> verseResults = new ArrayList<>();
}

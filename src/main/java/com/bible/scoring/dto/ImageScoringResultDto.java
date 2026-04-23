package com.bible.scoring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageScoringResultDto {

    private Long sessionId;
    private String participantName;
    private String contestName;
    private String bibleBook;
    private String bibleChapter;
    private Double accuracy;
    private Boolean passed;
    private Integer totalVerses;
    private Integer correctVerses;
    private Integer wrongVerses;
    private Integer totalChars;
    private Integer correctChars;
    private Integer wrongChars;
    private String ocrRawText;
    private List<VerseScoreItem> verseScores;

    @Getter
    @Setter
    public static class VerseScoreItem {
        private String ref;
        private String referenceText;
        private String ocrText;
        private Integer editDistance;
        private Boolean correct;
        private Integer refLength;
    }
}

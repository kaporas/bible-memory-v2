package com.bible.scoring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScoringResultDto {

    private Long sessionId;
    private String participantName;
    private String contestName;
    private String bibleBook;
    private String bibleChapter;

    private double accuracy;
    private boolean passed;

    private int totalVerses;
    private int correctVerses;
    private int wrongVerses;
    private int totalChars;
    private int correctChars;
    private int wrongChars;

    private List<VerseScoreItem> verseScores;

    @Getter
    @Setter
    public static class VerseScoreItem {
        private String ref;
        private String referenceText;
        private String inputText;
        private int editDistance;
        private boolean correct;
        private int refLength;
    }
}

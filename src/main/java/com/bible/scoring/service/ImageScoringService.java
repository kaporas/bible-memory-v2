package com.bible.scoring.service;

import com.bible.scoring.dto.ImageScoringResultDto;
import com.bible.scoring.dto.ImageVerseInputDto;
import com.bible.scoring.entity.ImageScoringSession;
import com.bible.scoring.entity.ImageVerseResult;
import com.bible.scoring.repository.ImageScoringSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageScoringService {

    private final ImageScoringSessionRepository sessionRepository;
    private final OcrService ocrService;

    private String normalize(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", "")
                .replaceAll("[.,!?;:'\"·~\\-–—“”‘’]", "")
                .trim();
    }

    private int editDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                        ? dp[i - 1][j - 1]
                        : 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[m][n];
    }

    @Transactional
    public ImageScoringResultDto score(
            String contestName, String participantName,
            String bibleBook, String bibleChapter,
            List<ImageVerseInputDto> verses,
            MultipartFile imageFile) {

        String ocrRaw = ocrService.extractText(imageFile);
        String ocrNorm = normalize(ocrRaw);

        List<String> refNorms = verses.stream()
                .map(v -> normalize(v.getReferenceText()))
                .toList();
        int totalRefChars = refNorms.stream().mapToInt(String::length).sum();
        int totalOcrChars = ocrNorm.length();

        List<ImageScoringResultDto.VerseScoreItem> verseScores = new ArrayList<>();
        int ocrOffset = 0;

        for (int i = 0; i < verses.size(); i++) {
            ImageVerseInputDto verse = verses.get(i);
            String refNorm = refNorms.get(i);
            int refLen = refNorm.length();

            // 기준 구절 길이 비율로 OCR 텍스트 분할
            int segLen = totalRefChars > 0
                    ? (int) Math.round((double) refLen / totalRefChars * totalOcrChars)
                    : 0;
            int segEnd = i == verses.size() - 1
                    ? totalOcrChars
                    : Math.min(ocrOffset + segLen, totalOcrChars);
            String ocrSeg = ocrOffset < totalOcrChars
                    ? ocrNorm.substring(ocrOffset, segEnd)
                    : "";
            ocrOffset = segEnd;

            int dist = editDistance(refNorm, ocrSeg);

            ImageScoringResultDto.VerseScoreItem item = new ImageScoringResultDto.VerseScoreItem();
            item.setRef(verse.getRef());
            item.setReferenceText(verse.getReferenceText());
            item.setOcrText(ocrSeg);
            item.setEditDistance(dist);
            item.setCorrect(dist == 0);
            item.setRefLength(refLen);
            verseScores.add(item);
        }

        int totalChars = verseScores.stream().mapToInt(ImageScoringResultDto.VerseScoreItem::getRefLength).sum();
        int wrongChars = verseScores.stream()
                .mapToInt(vs -> Math.min(vs.getEditDistance(), vs.getRefLength()))
                .sum();
        int correctChars = totalChars - wrongChars;
        int totalVerses = verseScores.size();
        int correctVerses = (int) verseScores.stream().filter(v -> Boolean.TRUE.equals(v.getCorrect())).count();
        double accuracy = totalChars > 0 ? (double) correctChars / totalChars * 100.0 : 0.0;
        double accRounded = Math.round(accuracy * 10.0) / 10.0;
        boolean passed = accuracy >= 95.0;

        ImageScoringSession session = new ImageScoringSession();
        session.setContestName(contestName);
        session.setParticipantName(participantName);
        session.setBibleBook(bibleBook);
        session.setBibleChapter(bibleChapter);
        session.setAccuracy(accRounded);
        session.setPassed(passed);
        session.setTotalVerses(totalVerses);
        session.setCorrectVerses(correctVerses);
        session.setTotalChars(totalChars);
        session.setCorrectChars(correctChars);
        session.setWrongChars(wrongChars);
        session.setImageFilename(imageFile.getOriginalFilename());
        session.setOcrRawText(ocrRaw);

        for (int i = 0; i < verseScores.size(); i++) {
            ImageScoringResultDto.VerseScoreItem vs = verseScores.get(i);
            ImageVerseResult vr = new ImageVerseResult();
            vr.setSession(session);
            vr.setVerseOrder(i + 1);
            vr.setVerseRef(vs.getRef());
            vr.setReferenceText(vs.getReferenceText());
            vr.setOcrText(vs.getOcrText());
            vr.setEditDistance(vs.getEditDistance());
            vr.setCorrect(vs.getCorrect());
            vr.setRefLength(vs.getRefLength());
            session.getVerseResults().add(vr);
        }

        ImageScoringSession saved = sessionRepository.save(session);

        ImageScoringResultDto result = new ImageScoringResultDto();
        result.setSessionId(saved.getId());
        result.setParticipantName(participantName);
        result.setContestName(contestName);
        result.setBibleBook(bibleBook);
        result.setBibleChapter(bibleChapter);
        result.setAccuracy(accRounded);
        result.setPassed(passed);
        result.setTotalVerses(totalVerses);
        result.setCorrectVerses(correctVerses);
        result.setWrongVerses(totalVerses - correctVerses);
        result.setTotalChars(totalChars);
        result.setCorrectChars(correctChars);
        result.setWrongChars(wrongChars);
        result.setOcrRawText(ocrRaw);
        result.setVerseScores(verseScores);

        return result;
    }

    @Transactional(readOnly = true)
    public List<ImageScoringSession> findAllSessions() {
        return sessionRepository.findAllByDeletedFalseOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public ImageScoringSession findSessionById(Long id) {
        return sessionRepository.findByIdWithResults(id)
                .orElseThrow(() -> new IllegalArgumentException("이미지 채점 세션을 찾을 수 없습니다: " + id));
    }

    @Transactional
    public void softDelete(Long id) {
        ImageScoringSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미지 채점 세션을 찾을 수 없습니다: " + id));
        session.setDeleted(true);
        sessionRepository.save(session);
    }
}

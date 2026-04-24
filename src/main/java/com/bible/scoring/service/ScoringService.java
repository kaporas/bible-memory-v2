package com.bible.scoring.service;

import com.bible.scoring.dto.ScoringRequest;
import com.bible.scoring.dto.ScoringResultDto;
import com.bible.scoring.dto.VerseInputDto;
import com.bible.scoring.entity.ScoringSession;
import com.bible.scoring.entity.VerseResult;
import com.bible.scoring.repository.ScoringSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final ScoringSessionRepository sessionRepository;

    // 공백 및 구두점 제거 후 정규화
    private String normalize(String s) {
        if (s == null) return "";
        return s.replaceAll("\\s+", "")
                .replaceAll("[.,!?;:'\"·~\\-–—“”‘’]", "")
                .trim();
    }

    // Levenshtein 편집 거리 계산
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
    public ScoringResultDto score(ScoringRequest request) {
        List<VerseInputDto> inputs = request.getVerses();
        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException("암송할 구절이 없습니다.");
        }

        List<ScoringResultDto.VerseScoreItem> verseScores = new ArrayList<>();
        for (VerseInputDto input : inputs) {
            String refNorm = normalize(input.getReferenceText());
            String inpNorm = normalize(input.getInputText() != null ? input.getInputText() : "");
            int dist = editDistance(refNorm, inpNorm);

            ScoringResultDto.VerseScoreItem item = new ScoringResultDto.VerseScoreItem();
            item.setRef(input.getRef());
            item.setReferenceText(input.getReferenceText());
            item.setInputText(input.getInputText() != null ? input.getInputText() : "");
            item.setEditDistance(dist);
            item.setCorrect(dist == 0);
            item.setRefLength(refNorm.length());
            verseScores.add(item);
        }

        int totalChars    = verseScores.stream().mapToInt(ScoringResultDto.VerseScoreItem::getRefLength).sum();
        // editDistance가 refLength를 초과할 수 있으므로 구절별로 cap 처리
        int wrongChars    = verseScores.stream()
                .mapToInt(vs -> Math.min(vs.getEditDistance(), vs.getRefLength()))
                .sum();
        int correctChars  = totalChars - wrongChars;
        int totalVerses   = verseScores.size();
        int correctVerses = (int) verseScores.stream().filter(ScoringResultDto.VerseScoreItem::isCorrect).count();
        double accuracy   = totalChars > 0 ? (double) correctChars / totalChars * 100.0 : 0.0;
        double accRounded = Math.round(accuracy * 10.0) / 10.0;
        boolean passed    = accuracy >= 95.0;

        ScoringSession session = new ScoringSession();
        session.setContestName(request.getContestName());
        session.setParticipantName(request.getParticipantName());
        session.setBibleBook(request.getBibleBook());
        session.setBibleChapter(request.getBibleChapter());
        session.setAccuracy(accRounded);
        session.setPassed(passed);
        session.setTotalVerses(totalVerses);
        session.setCorrectVerses(correctVerses);
        session.setTotalChars(totalChars);
        session.setCorrectChars(correctChars);
        session.setWrongChars(wrongChars);

        for (int idx = 0; idx < verseScores.size(); idx++) {
            ScoringResultDto.VerseScoreItem vs = verseScores.get(idx);
            VerseResult vr = new VerseResult();
            vr.setSession(session);
            vr.setVerseOrder(idx + 1);
            vr.setVerseRef(vs.getRef());
            vr.setReferenceText(vs.getReferenceText());
            vr.setInputText(vs.getInputText());
            vr.setEditDistance(vs.getEditDistance());
            vr.setCorrect(vs.isCorrect());
            vr.setRefLength(vs.getRefLength());
            session.getVerseResults().add(vr);
        }

        ScoringSession saved = sessionRepository.save(session);

        ScoringResultDto result = new ScoringResultDto();
        result.setSessionId(saved.getId());
        result.setParticipantName(request.getParticipantName());
        result.setContestName(request.getContestName());
        result.setBibleBook(request.getBibleBook());
        result.setBibleChapter(request.getBibleChapter());
        result.setAccuracy(accRounded);
        result.setPassed(passed);
        result.setTotalVerses(totalVerses);
        result.setCorrectVerses(correctVerses);
        result.setWrongVerses(totalVerses - correctVerses);
        result.setTotalChars(totalChars);
        result.setCorrectChars(correctChars);
        result.setWrongChars(wrongChars);
        result.setVerseScores(verseScores);

        return result;
    }

    @Transactional(readOnly = true)
    public List<ScoringSession> findAllSessions() {
        return sessionRepository.findAllByDeletedFalseOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public ScoringSession findSessionById(Long id) {
        return sessionRepository.findByIdWithResults(id)
                .orElseThrow(() -> new IllegalArgumentException("채점 세션을 찾을 수 없습니다: " + id));
    }

    @Transactional
    public void softDelete(Long id) {
        ScoringSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채점 세션을 찾을 수 없습니다: " + id));
        session.setDeleted(true);
        sessionRepository.save(session);
    }
}

-- 성경암송대회 채점 시스템 DB 스키마
-- MySQL 8.x 기준

CREATE DATABASE IF NOT EXISTS bible_scoring
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bible_scoring;

-- 채점 세션 테이블
CREATE TABLE IF NOT EXISTS scoring_sessions (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '채점 세션 ID',
    contest_name     VARCHAR(100) NOT NULL                   COMMENT '대회명',
    participant_name VARCHAR(50)  NOT NULL                   COMMENT '참가자명',
    bible_book       VARCHAR(50)                             COMMENT '성경 책',
    bible_chapter    VARCHAR(100)                            COMMENT '장/절 범위',
    accuracy         DOUBLE       NOT NULL                   COMMENT '글자 정확도(%)',
    passed           TINYINT(1)   NOT NULL                   COMMENT '합격 여부 (1=합격, 0=불합격)',
    total_verses     INT          NOT NULL                   COMMENT '전체 구절 수',
    correct_verses   INT          NOT NULL                   COMMENT '완전 정답 구절 수',
    total_chars      INT          NOT NULL                   COMMENT '전체 글자 수',
    correct_chars    INT          NOT NULL                   COMMENT '맞은 글자 수',
    wrong_chars      INT          NOT NULL                   COMMENT '틀린 글자 수',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '채점 시각',

    INDEX idx_participant (participant_name),
    INDEX idx_created_at  (created_at),
    INDEX idx_passed      (passed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채점 세션';

-- 구절별 채점 결과 테이블
CREATE TABLE IF NOT EXISTS verse_results (
    id             BIGINT      AUTO_INCREMENT PRIMARY KEY COMMENT '구절 결과 ID',
    session_id     BIGINT      NOT NULL                   COMMENT '채점 세션 ID (FK)',
    verse_order    INT         NOT NULL                   COMMENT '구절 순서',
    verse_ref      VARCHAR(20) NOT NULL                   COMMENT '구절 참조 (예: 1절)',
    reference_text TEXT        NOT NULL                   COMMENT '기준 구절 원문',
    input_text     TEXT                                   COMMENT '참가자 입력 내용',
    edit_distance  INT         NOT NULL                   COMMENT '편집 거리 (Levenshtein)',
    correct        TINYINT(1)  NOT NULL                   COMMENT '완전 정답 여부',
    ref_length     INT         NOT NULL                   COMMENT '기준 글자 수 (정규화 후)',

    FOREIGN KEY (session_id) REFERENCES scoring_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_order (session_id, verse_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='구절별 채점 결과';

-- 사진 파일 제출 테이블
CREATE TABLE IF NOT EXISTS photo_submissions (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY            COMMENT '제출 ID',
    name         VARCHAR(100) NOT NULL                              COMMENT '이름 (필수)',
    note         TEXT                                               COMMENT '비고',
    file_name    VARCHAR(255) NOT NULL                              COMMENT '원본 파일명',
    file_type    VARCHAR(100)                                       COMMENT '파일 MIME 타입',
    file_size    BIGINT                                             COMMENT '파일 크기 (bytes)',
    file_data    MEDIUMBLOB   NOT NULL                              COMMENT '파일 데이터',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP   COMMENT '업로드 시각',
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP           COMMENT '수정 시각',

    INDEX idx_photo_name       (name),
    INDEX idx_photo_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사진 파일 제출';

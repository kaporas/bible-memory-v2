-- 이미지 채점 세션 테이블
CREATE TABLE image_scoring_sessions (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    contest_name      VARCHAR(100)  NOT NULL,
    participant_name  VARCHAR(50)   NOT NULL,
    bible_book        VARCHAR(50),
    bible_chapter     VARCHAR(100),
    accuracy          DOUBLE        NOT NULL,
    passed            TINYINT(1)    NOT NULL,
    total_verses      INT           NOT NULL,
    correct_verses    INT           NOT NULL,
    total_chars       INT           NOT NULL,
    correct_chars     INT           NOT NULL,
    wrong_chars       INT           NOT NULL,
    image_filename    VARCHAR(255),
    ocr_raw_text      TEXT,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 이미지 채점 구절별 결과 테이블
CREATE TABLE image_verse_results (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id      BIGINT   NOT NULL,
    verse_order     INT      NOT NULL,
    verse_ref       VARCHAR(20)  NOT NULL,
    reference_text  TEXT         NOT NULL,
    ocr_text        TEXT,
    edit_distance   INT          NOT NULL,
    correct         TINYINT(1)   NOT NULL,
    ref_length      INT          NOT NULL,
    CONSTRAINT fk_image_verse_session
        FOREIGN KEY (session_id) REFERENCES image_scoring_sessions(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

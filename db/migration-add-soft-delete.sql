-- Soft Delete 컬럼 추가 마이그레이션
-- 실행 대상 DB: bible_scoring
-- 실행 순서대로 적용하세요

USE bible_scoring;

ALTER TABLE scoring_sessions
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '소프트 삭제 여부 (1=삭제됨)';

ALTER TABLE image_scoring_sessions
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '소프트 삭제 여부 (1=삭제됨)';

ALTER TABLE photo_submissions
    ADD COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '소프트 삭제 여부 (1=삭제됨)';

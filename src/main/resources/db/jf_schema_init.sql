-- JapaFlow 学习进度表初始化脚本
-- 在已有数据库下直接执行即可

CREATE TABLE IF NOT EXISTS jf_word_learning (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  word_id         VARCHAR(32)   NOT NULL COMMENT '如 w1, w2, ...',

  main_status     VARCHAR(16)   NOT NULL DEFAULT 'new' COMMENT 'new/learning/review/mastered',
  slashed         TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否已斩（手动标记已掌握）',

  meaning_to_word_correct  TINYINT(1) NOT NULL DEFAULT 0,
  audio_to_word_correct    TINYINT(1) NOT NULL DEFAULT 0,
  word_to_meaning_correct  TINYINT(1) NOT NULL DEFAULT 0,
  pronunciation_passed     TINYINT(1) NOT NULL DEFAULT 0,

  pronunciation_score   SMALLINT NOT NULL DEFAULT 0,
  accuracy_score        SMALLINT NOT NULL DEFAULT 0,
  fluency_score         SMALLINT NOT NULL DEFAULT 0,
  completeness_score    SMALLINT NOT NULL DEFAULT 0,

  diagnostic_tags       JSON     COMMENT '如 ["听音弱","发音不标准"]',
  pronunciation_reasons JSON     COMMENT '如 ["发音不标准","流畅度不足"]',
  recognized_text       VARCHAR(512) DEFAULT '',

  attempts_meaning_to_word  INT NOT NULL DEFAULT 0,
  attempts_audio_to_word    INT NOT NULL DEFAULT 0,
  attempts_word_to_meaning  INT NOT NULL DEFAULT 0,
  attempts_pronunciation    INT NOT NULL DEFAULT 0,

  last_practiced_at  DATETIME     NULL,
  created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_word (user_id, lesson_id, word_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_grammar_practice (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  grammar_id      VARCHAR(32)   NOT NULL COMMENT '如 g1, g2',
  example_index   INT           NOT NULL DEFAULT 0 COMMENT '该语法点下第几个例句',

  answer          TEXT          COMMENT '用户输入的翻译答案',
  submitted       TINYINT(1)    NOT NULL DEFAULT 0,
  correct         TINYINT(1)    NOT NULL DEFAULT 0,
  revealed        TINYINT(1)    NOT NULL DEFAULT 0,
  attempts        INT           NOT NULL DEFAULT 0,

  pronunciation_passed      TINYINT(1) NOT NULL DEFAULT 0,
  pronunciation_score       SMALLINT   NOT NULL DEFAULT 0,
  accuracy_score            SMALLINT   NOT NULL DEFAULT 0,
  fluency_score             SMALLINT   NOT NULL DEFAULT 0,
  completeness_score        SMALLINT   NOT NULL DEFAULT 0,
  pronunciation_reasons     JSON,
  recognized_text           VARCHAR(512) DEFAULT '',
  pronunciation_attempts    INT        NOT NULL DEFAULT 0,

  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_grammar (user_id, lesson_id, grammar_id, example_index),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_sentence_practice (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  sentence_id     VARCHAR(32)   NOT NULL COMMENT '如 s1, s2',

  pronunciation_passed      TINYINT(1) NOT NULL DEFAULT 0,
  pronunciation_score       SMALLINT   NOT NULL DEFAULT 0,
  accuracy_score            SMALLINT   NOT NULL DEFAULT 0,
  fluency_score             SMALLINT   NOT NULL DEFAULT 0,
  completeness_score        SMALLINT   NOT NULL DEFAULT 0,
  pronunciation_reasons     JSON,
  recognized_text           VARCHAR(512) DEFAULT '',
  pronunciation_attempts    INT        NOT NULL DEFAULT 0,

  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_sentence (user_id, lesson_id, sentence_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_exercise_result (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  exercise_id     VARCHAR(32)   NOT NULL COMMENT '练习题 ID',
  group_index     INT           NOT NULL DEFAULT 0 COMMENT '所属分组索引',

  answer          TEXT          COMMENT '用户答案',
  correct         TINYINT(1)    NOT NULL DEFAULT 0,
  submitted_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_exercise (user_id, lesson_id, exercise_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_wrong_book (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  item_type       VARCHAR(16)   NOT NULL COMMENT 'word/sentence/grammar/exercise',
  item_id         VARCHAR(32)   NOT NULL COMMENT '对应 word_id/sentence_id/grammar_id/exercise_id',

  wrong_detail    JSON          COMMENT '错误详情快照',
  resolved        TINYINT(1)    NOT NULL DEFAULT 0,
  created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  resolved_at     DATETIME      NULL,

  UNIQUE KEY uk_user_lesson_item (user_id, lesson_id, item_type, item_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_interaction_progress (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  item_type       VARCHAR(16)   NOT NULL COMMENT 'word/sentence/grammarExample',
  item_id         VARCHAR(32)   NOT NULL,

  pronunciation_state  VARCHAR(16) DEFAULT '' COMMENT 'passed/retry/空',
  skipped              TINYINT(1)  NOT NULL DEFAULT 0,
  detail               JSON        COMMENT '完整交互快照',

  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_interaction (user_id, lesson_id, item_type, item_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_study_time (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  module          VARCHAR(16)   NOT NULL COMMENT 'vocab/grammar/text/exercises/wrongbook/favorites',

  total_ms        BIGINT        NOT NULL DEFAULT 0,
  last_started_at DATETIME      NULL,
  last_active_at  DATETIME      NULL,
  updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_module (user_id, lesson_id, module),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_favorite (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  item_type       VARCHAR(16)   NOT NULL COMMENT 'word/sentence',
  item_id         VARCHAR(32)   NOT NULL,

  snapshot        JSON          NOT NULL COMMENT '收藏时的快照（jp/kana/cn/text/translation/voiceId 等）',
  saved_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_fav (user_id, lesson_id, item_type, item_id),
  KEY idx_user (user_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS jf_lesson_preference (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,

  current_voice_id     VARCHAR(64)  DEFAULT '',
  playback_rate        DECIMAL(3,2) DEFAULT 1.00,
  vocab_focus_only     TINYINT(1)   NOT NULL DEFAULT 0,
  current_exercise_group INT        NOT NULL DEFAULT 0,
  text_current_tab     VARCHAR(32)  DEFAULT 'basic',

  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

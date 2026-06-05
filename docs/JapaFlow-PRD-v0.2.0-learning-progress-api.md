# JapaFlow PRD v0.2.0 — 学习进度 Java API

> 目标：将前端 localStorage 中的学习数据迁移到 MySQL 持久化，通过 Java RESTful API 提供读写能力，实现跨设备同步。

---

## 1. 背景

当前 JapaFlow 的所有学习进度存储在浏览器 localStorage 中（key 前缀 `japaflow:`），数据包括：

| localStorage key 模式 | 说明 |
|---|---|
| `lesson:{id}:wordLearning` | 每个单词的学习状态（发音/听音/识别是否通过、得分、诊断标签等） |
| `lesson:{id}:wordProgress` | 单词掌握状态（unseen/unfamiliar/familiar） |
| `lesson:{id}:grammarPractice` | 语法例句练习状态（翻译正确、发音通过、得分等） |
| `lesson:{id}:sentencePractice` | 课文句子跟读状态（发音得分、是否通过） |
| `lesson:{id}:exerciseResults` | 练习题答题结果 |
| `lesson:{id}:exerciseGroupAnswers` | 练习分组答案 |
| `lesson:{id}:exerciseGroupSubmitted` | 练习分组提交状态 |
| `lesson:{id}:wrongBook` | 错题集 |
| `lesson:{id}:interactionProgress` | 交互进度（words/sentences/grammarExamples 的 retry/skipped 状态） |
| `lesson:{id}:studyTime` | 各模块学习时长（vocab/grammar/text/exercises/wrongbook/favorites） |
| `lesson:{id}:favorites` | 收藏的单词和句子 |
| `lesson:{id}:currentVoiceId` | 当前选择的语音 |
| `lesson:{id}:playbackRate` | 播放速度 |
| `lesson:{id}:vocabFocusOnly` | 是否仅显示未掌握单词 |
| `lesson:{id}:vocabTestQueue` | 单词测试队列 |
| `lesson:{id}:currentExerciseGroup` | 当前练习分组索引 |
| `lesson:{id}:textCurrentTab` | 课文当前 tab |

**痛点**：换设备/清浏览器数据后进度丢失，无法跨端同步。

---

## 2. MySQL 表结构设计

### 2.1 用户表（复用已有系统）

> JapaFlow 通过 auth-bridge.js 从主站 (groundedglow.cc) 同步 token 和 user 信息。user_id 直接复用主站用户系统，**不需要在 JapaFlow 后端新建用户表**。

所有表通过 `user_id BIGINT NOT NULL` 关联用户。

---

### 2.2 单词学习记录 `jf_word_learning`

每条记录 = 一个用户 + 一课 + 一个单词的学习状态。

```sql
CREATE TABLE jf_word_learning (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT        NOT NULL,
  lesson_id       INT           NOT NULL,
  word_id         VARCHAR(32)   NOT NULL COMMENT '如 w1, w2, ...',

  -- 掌握状态
  main_status     VARCHAR(16)   NOT NULL DEFAULT 'new' COMMENT 'new/learning/review/mastered',
  slashed         TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否已斩（手动标记已掌握）',

  -- 各维度通过情况
  meaning_to_word_correct  TINYINT(1) NOT NULL DEFAULT 0,
  audio_to_word_correct    TINYINT(1) NOT NULL DEFAULT 0,
  word_to_meaning_correct  TINYINT(1) NOT NULL DEFAULT 0,
  pronunciation_passed     TINYINT(1) NOT NULL DEFAULT 0,

  -- 发音评分
  pronunciation_score   SMALLINT NOT NULL DEFAULT 0,
  accuracy_score        SMALLINT NOT NULL DEFAULT 0,
  fluency_score         SMALLINT NOT NULL DEFAULT 0,
  completeness_score    SMALLINT NOT NULL DEFAULT 0,

  -- 诊断
  diagnostic_tags       JSON     COMMENT '如 ["听音弱","发音不标准"]',
  pronunciation_reasons JSON     COMMENT '如 ["发音不标准","流畅度不足"]',
  recognized_text       VARCHAR(512) DEFAULT '',

  -- 各维度尝试次数
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
```

---

### 2.3 语法练习记录 `jf_grammar_practice`

每条记录 = 一个用户 + 一课 + 一个语法例句的练习状态。

```sql
CREATE TABLE jf_grammar_practice (
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

  -- 发音评分
  pronunciation_passed      TINYINT(1) NOT NULL DEFAULT 0,
  pronunciation_score       SMALLINT   NOT NULL DEFAULT 0,
  accuracy_score            SMALLINT   NOT NULL DEFAULT 0,
  fluency_score             SMALLINT   NOT NULL DEFAULT 0,
  completeness_score        SMALLINT   NOT NULL DEFAULT 0,
  pronunciation_reasons     JSON,
  recognized_text           VARCHAR(512) DEFAULT '',
  pronunciation_attempts    INT        NOT NULL DEFAULT 0,

  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_grammar (user_id, lesson_id, grammar_id, example_index),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

### 2.4 课文句子练习记录 `jf_sentence_practice`

```sql
CREATE TABLE jf_sentence_practice (
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

  updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  UNIQUE KEY uk_user_lesson_sentence (user_id, lesson_id, sentence_id),
  KEY idx_user_lesson (user_id, lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

### 2.5 练习题结果 `jf_exercise_result`

```sql
CREATE TABLE jf_exercise_result (
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
```

---

### 2.6 错题集 `jf_wrong_book`

```sql
CREATE TABLE jf_wrong_book (
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
```

---

### 2.7 交互进度 `jf_interaction_progress`

跟踪 words/sentences/grammarExamples 的跟读交互状态（retry/skipped），用于计算"待复习"数量。

```sql
CREATE TABLE jf_interaction_progress (
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
```

---

### 2.8 学习时长 `jf_study_time`

```sql
CREATE TABLE jf_study_time (
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
```

---

### 2.9 收藏 `jf_favorite`

```sql
CREATE TABLE jf_favorite (
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
```

---

### 2.10 用户课程偏好 `jf_lesson_preference`

存储播放速度、语音选择等轻量偏好设置。

```sql
CREATE TABLE jf_lesson_preference (
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
```

---

## 3. API 设计

### 3.1 通用约定

- **Base URL**: `/api/japaflow`
- **认证**: 所有接口需携带 `Authorization: Bearer <token>`，后端从 token 解析 `user_id`
- **响应格式**:
  ```json
  {
    "code": 0,
    "message": "success",
    "data": { ... }
  }
  ```
- **错误码**: `0` 成功，`401` 未认证，`400` 参数错误，`500` 服务端错误

---

### 3.2 全量同步（导出 / 导入）

> 替代当前的 JSON 文件导出/导入功能。前端首次登录时拉取全量数据；之后通过细粒度接口增量更新。

#### 3.2.1 导出全量进度

```
GET /api/japaflow/progress/export
```

**响应**:
```json
{
  "code": 0,
  "data": {
    "exportedAt": "2026-06-02T10:00:00Z",
    "lessons": {
      "27": {
        "wordLearning": { "w1": { ... }, "w2": { ... } },
        "grammarPractice": { "g1_0": { ... } },
        "sentencePractice": { "s1": { ... } },
        "exerciseResults": [ { "exerciseId": "e1", ... } ],
        "wrongBook": { ... },
        "interactionProgress": { "words": {}, "sentences": {}, "grammarExamples": {} },
        "studyTime": { "vocab": { "totalMs": 120000 }, ... },
        "favorites": { "words": {}, "sentences": {} },
        "preferences": { "currentVoiceId": "Japanese_KindLady", "playbackRate": 1.0 }
      }
    }
  }
}
```

#### 3.2.2 导入全量进度

```
POST /api/japaflow/progress/import
Content-Type: application/json
```

**请求体**: 与导出格式相同的 `lessons` 对象。导入为 **全量覆盖**（先删后插）。

**响应**:
```json
{ "code": 0, "message": "导入成功", "data": { "lessonsImported": 3 } }
```

---

### 3.3 单词学习

#### 3.3.1 查询某课全部单词学习状态

```
GET /api/japaflow/lessons/{lessonId}/words
```

**响应**:
```json
{
  "code": 0,
  "data": [
    {
      "wordId": "w1",
      "mainStatus": "mastered",
      "slashed": false,
      "meaningToWordCorrect": true,
      "audioToWordCorrect": true,
      "wordToMeaningCorrect": true,
      "pronunciationPassed": true,
      "pronunciationScore": 92,
      "accuracyScore": 95,
      "fluencyScore": 88,
      "completenessScore": 100,
      "diagnosticTags": [],
      "attempts": { "meaningToWord": 2, "audioToWord": 1, "wordToMeaning": 1, "pronunciation": 3 },
      "lastPracticedAt": "2026-06-01T14:30:00Z"
    }
  ]
}
```

#### 3.3.2 更新单个单词学习状态

```
PUT /api/japaflow/lessons/{lessonId}/words/{wordId}
Content-Type: application/json
```

**请求体**（部分更新，只传需要变更的字段）:
```json
{
  "pronunciationPassed": true,
  "pronunciationScore": 92,
  "accuracyScore": 95,
  "fluencyScore": 88,
  "completenessScore": 100,
  "attempts": { "pronunciation": 3 }
}
```

**响应**:
```json
{ "code": 0, "data": { "wordId": "w1", "mainStatus": "mastered", ... } }
```

> 后端在保存时自动根据业务规则计算 `mainStatus` 和 `diagnosticTags`。

#### 3.3.3 重置某课单词学习数据

```
DELETE /api/japaflow/lessons/{lessonId}/words
```

---

### 3.4 语法练习

#### 3.4.1 查询某课语法练习状态

```
GET /api/japaflow/lessons/{lessonId}/grammar
```

#### 3.4.2 更新语法例句练习状态

```
PUT /api/japaflow/lessons/{lessonId}/grammar/{grammarId}/{exampleIndex}
```

**请求体**:
```json
{
  "answer": "子供の時、大きな地震がありました。",
  "submitted": true,
  "correct": true,
  "pronunciationPassed": true,
  "pronunciationScore": 88,
  "attempts": 2,
  "pronunciationAttempts": 1
}
```

---

### 3.5 课文句子练习

#### 3.5.1 查询某课句子练习状态

```
GET /api/japaflow/lessons/{lessonId}/sentences
```

#### 3.5.2 更新句子练习状态

```
PUT /api/japaflow/lessons/{lessonId}/sentences/{sentenceId}
```

**请求体**:
```json
{
  "pronunciationPassed": true,
  "pronunciationScore": 90,
  "accuracyScore": 93,
  "fluencyScore": 85,
  "completenessScore": 100,
  "pronunciationAttempts": 2
}
```

---

### 3.6 练习题

#### 3.6.1 查询某课练习结果

```
GET /api/japaflow/lessons/{lessonId}/exercises
```

#### 3.6.2 提交练习答案

```
POST /api/japaflow/lessons/{lessonId}/exercises/{exerciseId}
```

**请求体**:
```json
{
  "groupIndex": 0,
  "answer": "けいざい",
  "correct": true
}
```

#### 3.6.3 清空某课练习结果

```
DELETE /api/japaflow/lessons/{lessonId}/exercises
```

---

### 3.7 错题集

#### 3.7.1 查询某课错题

```
GET /api/japaflow/lessons/{lessonId}/wrong-book
```

#### 3.7.2 添加错题

```
POST /api/japaflow/lessons/{lessonId}/wrong-book
```

**请求体**:
```json
{
  "itemType": "word",
  "itemId": "w3",
  "wrongDetail": { "userAnswer": "がくぶ", "correctAnswer": "がくぶ" }
}
```

#### 3.7.3 标记错题为已解决

```
PUT /api/japaflow/lessons/{lessonId}/wrong-book/{itemType}/{itemId}/resolve
```

#### 3.7.4 清空某课错题

```
DELETE /api/japaflow/lessons/{lessonId}/wrong-book
```

---

### 3.8 交互进度

#### 3.8.1 查询某课交互进度

```
GET /api/japaflow/lessons/{lessonId}/interaction-progress
```

**响应**:
```json
{
  "code": 0,
  "data": {
    "words": { "w1": { "pronunciationState": "passed", "skipped": false } },
    "sentences": { "s2": { "pronunciationState": "retry", "skipped": false } },
    "grammarExamples": {}
  }
}
```

#### 3.8.2 更新交互进度项

```
PUT /api/japaflow/lessons/{lessonId}/interaction-progress/{itemType}/{itemId}
```

**请求体**:
```json
{
  "pronunciationState": "retry",
  "skipped": false
}
```

---

### 3.9 学习时长

#### 3.9.1 查询某课各模块学习时长

```
GET /api/japaflow/lessons/{lessonId}/study-time
```

**响应**:
```json
{
  "code": 0,
  "data": {
    "vocab":     { "totalMs": 300000, "lastActiveAt": "2026-06-01T15:00:00Z" },
    "grammar":   { "totalMs": 180000, "lastActiveAt": null },
    "text":      { "totalMs": 0, "lastActiveAt": null },
    "exercises": { "totalMs": 0, "lastActiveAt": null },
    "wrongbook": { "totalMs": 0, "lastActiveAt": null },
    "favorites": { "totalMs": 0, "lastActiveAt": null }
  }
}
```

#### 3.9.2 累加学习时长

```
POST /api/japaflow/lessons/{lessonId}/study-time/{module}
```

**请求体**:
```json
{
  "deltaMs": 45000,
  "activeAt": "2026-06-01T15:01:00Z"
}
```

> 使用增量累加（`totalMs += deltaMs`）而非全量覆盖，避免并发冲突。

---

### 3.10 收藏

#### 3.10.1 查询所有收藏

```
GET /api/japaflow/favorites
```

**Query 参数**: `lessonId` (可选，不传则返回全部课程的收藏)

#### 3.10.2 添加收藏

```
POST /api/japaflow/lessons/{lessonId}/favorites
```

**请求体**:
```json
{
  "itemType": "word",
  "itemId": "w5",
  "snapshot": {
    "jp": "日記",
    "kana": "にっき",
    "cn": "日记",
    "voiceId": "Japanese_KindLady"
  }
}
```

#### 3.10.3 取消收藏

```
DELETE /api/japaflow/lessons/{lessonId}/favorites/{itemType}/{itemId}
```

---

### 3.11 课程偏好

#### 3.11.1 查询偏好

```
GET /api/japaflow/lessons/{lessonId}/preferences
```

#### 3.11.2 更新偏好

```
PUT /api/japaflow/lessons/{lessonId}/preferences
```

**请求体**（部分更新）:
```json
{
  "currentVoiceId": "Japanese_KindLady",
  "playbackRate": 1.5
}
```

---

### 3.12 课程进度总览

> 用于首页课程列表展示各课完成度。

```
GET /api/japaflow/progress/summary
```

**Query 参数**: `lessonIds` (可选，逗号分隔，不传则返回所有有记录的课程)

**响应**:
```json
{
  "code": 0,
  "data": [
    {
      "lessonId": 27,
      "vocab":     { "completed": 40, "total": 52 },
      "grammar":   { "completed": 5, "total": 8 },
      "text":      { "completed": 10, "total": 14 },
      "exercises": { "completed": 3, "total": 6 },
      "weak":      { "total": 5 },
      "percent": 72,
      "status": "learning",
      "totalStudyTimeMs": 960000
    }
  ]
}
```

> `total` 字段（vocab/grammar/text/exercises 的 total）从课程数据 catalog 获取，不存数据库。前端传入或后端从 catalog.json 读取。

---

## 4. 前端对接要点

### 4.1 数据同步策略

```
用户登录 → GET /progress/export 全量拉取 → 写入 localStorage
                   ↓
          后续操作实时调用 PUT/POST 接口 → 同时更新 localStorage
                   ↓
用户切换设备 → GET /progress/export 拉取最新数据
```

- **离线降级**：接口调用失败时仍写 localStorage，下次在线时批量同步
- **冲突策略**：以服务端数据为准（last-write-wins），`updated_at` 较新的记录覆盖较旧的

### 4.2 前端改造清单

1. 每个 `write()` / `removeStored()` 调用处，增加对应 API 调用
2. `exportLearningData()` 改为调用 `GET /progress/export`
3. `importLearningData()` 改为调用 `POST /progress/import`
4. `settleStudyTimer()` 结算时调用 `POST /study-time/{module}`
5. `toggleFavorite()` 调用收藏 API
6. 课程列表页使用 `GET /progress/summary` 获取进度概览

---

## 5. 实现注意事项

### 5.1 Java 项目建议

- 框架：Spring Boot 3.x + MyBatis-Plus / JPA
- 参数校验：`@Valid` + JSR 303
- 认证：复用主站 JWT token 解析逻辑
- 表前缀 `jf_` 避免与主站表冲突

### 5.2 性能考虑

- 单词/语法/句子练习的更新频率较高（用户每做一步都可能触发），使用 `INSERT ... ON DUPLICATE KEY UPDATE` 保证幂等
- 学习时长使用增量累加 `total_ms = total_ms + #{deltaMs}`，避免并发覆盖
- 进度总览接口可能涉及多表 join，考虑缓存或汇总表
- 前端可做 debounce（如 2 秒内多次操作合并为一次请求）

### 5.3 数据迁移

首次上线时需要提供一个一次性的迁移入口：
1. 前端导出 localStorage 全量 JSON
2. 调用 `POST /progress/import` 写入服务端
3. 后续所有操作走 API

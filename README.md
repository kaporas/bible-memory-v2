# 성경암송대회 채점 시스템

Spring Boot MVC 기반의 성경암송대회 채점 시스템입니다.

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.3 |
| ORM | Spring Data JPA (Hibernate) |
| DB | MySQL 8.x |
| View | Thymeleaf |
| Validation | Spring Validation (jakarta.validation) |
| Build | Maven |

## 프로젝트 구조

```
bible-scoring/
├── db/
│   └── schema.sql                    # DB 생성 및 테이블 스크립트
├── src/main/java/com/bible/scoring/
│   ├── BibleScoringApplication.java
│   ├── controller/
│   │   ├── MainController.java       # 뷰 컨트롤러 (GET /, /history, /history/{id})
│   │   ├── ScoringApiController.java # REST API (POST /api/score)
│   │   └── GlobalExceptionHandler.java # Validation 오류 처리
│   ├── entity/
│   │   ├── ScoringSession.java       # 채점 세션 엔티티
│   │   └── VerseResult.java          # 구절별 결과 엔티티
│   ├── dto/
│   │   ├── ScoringRequest.java       # 채점 요청 DTO (Validation 포함)
│   │   ├── VerseInputDto.java        # 구절 입력 DTO
│   │   └── ScoringResultDto.java     # 채점 결과 DTO
│   ├── repository/
│   │   ├── ScoringSessionRepository.java
│   │   └── VerseResultRepository.java
│   └── service/
│       └── ScoringService.java       # 채점 비즈니스 로직
└── src/main/resources/
    ├── application.yml
    └── templates/
        ├── index.html          # 메인 채점 화면
        ├── history.html        # 채점 이력 목록
        └── history-detail.html # 채점 이력 상세
```

## 주요 기능

### 채점 시스템
- **3단계 채점 흐름**: 기준 구절 설정 → 암송 입력 → 채점 결과
- **구절 프리셋**: 시편 119편 (1~32절, 구간별), 시편 23편 제공
- **Levenshtein 편집 거리** 알고리즘으로 글자 단위 정확도 계산
- **합격 기준**: 전체 글자 수의 95% 이상 정확도

### Validation (이름 필드)
```java
@NotBlank(message = "참가자 이름을 입력해주세요")
@Size(min = 2, max = 20, message = "이름은 2~20자 사이로 입력해주세요")
@Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글 또는 영문만 입력해주세요")
private String participantName;
```
- 서버 응답 400 시 `{ "fieldErrors": { "participantName": "..." } }` 반환
- 클라이언트에서 인라인 오류 메시지 표시

### 채점 이력
- 모든 채점 결과를 MySQL에 저장
- `/history`: 전체 이력 목록 (합격/불합격, 정확도, 날짜)
- `/history/{id}`: 구절별 상세 결과

### 메일 전송
- EmailJS를 활용한 채점 결과 메일 전송

## 실행 방법

### 1. MySQL DB 설정

```sql
-- MySQL에서 실행
source db/schema.sql;
```

또는 직접 실행:
```bash
mysql -u root -p < db/schema.sql
```

### 2. application.yml 설정

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bible_scoring?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: root       # DB 사용자명으로 변경
    password: password   # DB 비밀번호로 변경
```

### 3. 빌드 및 실행

```bash
# 프로젝트 루트에서
mvn clean package
java -jar target/scoring-1.0.0.jar

# 또는 개발 모드
mvn spring-boot:run
```

### 4. 접속

```
http://localhost:8080
```

## API 명세

### POST /api/score

채점 요청 및 결과 저장

**Request Body:**
```json
{
  "participantName": "홍길동",
  "contestName": "2026년 성경암송대회",
  "bibleBook": "시편",
  "bibleChapter": "119편 1~8절",
  "verses": [
    {
      "ref": "1절",
      "referenceText": "행위가 온전하여 여호와의 율법을 따라 행하는 자들은 복이 있음이여",
      "inputText": "행위가 온전하여 여호와의 율법을 따라 행하는 자들은 복이 있음이여"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "sessionId": 1,
  "participantName": "홍길동",
  "accuracy": 100.0,
  "passed": true,
  "totalVerses": 1,
  "correctVerses": 1,
  "wrongVerses": 0,
  "totalChars": 30,
  "correctChars": 30,
  "wrongChars": 0,
  "verseScores": [
    {
      "ref": "1절",
      "editDistance": 0,
      "correct": true,
      "refLength": 30
    }
  ]
}
```

**Response (400 Bad Request - Validation 오류):**
```json
{
  "error": "VALIDATION_FAILED",
  "fieldErrors": {
    "participantName": "참가자 이름을 입력해주세요"
  }
}
```

## DB 스키마

### scoring_sessions (채점 세션)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | 채점 세션 ID |
| contest_name | VARCHAR(100) | 대회명 |
| participant_name | VARCHAR(50) | 참가자명 |
| bible_book | VARCHAR(50) | 성경 책 |
| bible_chapter | VARCHAR(100) | 장/절 범위 |
| accuracy | DOUBLE | 글자 정확도(%) |
| passed | TINYINT(1) | 합격 여부 |
| total_verses | INT | 전체 구절 수 |
| correct_verses | INT | 완전 정답 구절 수 |
| total_chars | INT | 전체 글자 수 |
| correct_chars | INT | 맞은 글자 수 |
| wrong_chars | INT | 틀린 글자 수 |
| created_at | DATETIME | 채점 시각 |

### verse_results (구절별 채점 결과)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT PK | 구절 결과 ID |
| session_id | BIGINT FK | 채점 세션 ID |
| verse_order | INT | 구절 순서 |
| verse_ref | VARCHAR(20) | 구절 참조 (예: 1절) |
| reference_text | TEXT | 기준 구절 원문 |
| input_text | TEXT | 참가자 입력 내용 |
| edit_distance | INT | 편집 거리 (Levenshtein) |
| correct | TINYINT(1) | 완전 정답 여부 |
| ref_length | INT | 기준 글자 수 (정규화 후) |

## MVC 패턴 구조

```
Client Request
     │
     ▼
┌─────────────────────┐
│    Controller Layer  │  MainController (뷰)
│                     │  ScoringApiController (REST)
└─────────────────────┘
     │
     ▼
┌─────────────────────┐
│    Service Layer     │  ScoringService
│  (비즈니스 로직)     │  - normalize()
│                     │  - editDistance() (Levenshtein)
│                     │  - score()
└─────────────────────┘
     │
     ▼
┌─────────────────────┐
│  Repository Layer    │  ScoringSessionRepository
│  (데이터 접근)       │  VerseResultRepository
└─────────────────────┘
     │
     ▼
┌─────────────────────┐
│     MySQL DB         │  bible_scoring
└─────────────────────┘
```

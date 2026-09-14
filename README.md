# API Health Monitor

Spring Boot 기반 **API 실행 이력 및 장애 모니터링 서비스**입니다.

단순 CRUD 프로젝트가 아니라, 백엔드 서비스 운영 과정에서 필요한  
**API 실행 결과 저장 → 성공/실패 이력 관리 → 일별 통계 집계 → 조회** 흐름을 구현했습니다.

---

## 1. 프로젝트 개요

API 실행 결과를 기록하고 성공/실패 여부, HTTP 상태 코드, 응답시간, 오류 정보를 관리하는 REST API 서비스입니다.

저장된 실행 이력을 기반으로 일별 호출량, 성공 건수, 실패 건수, 성공률, 평균 응답시간을 집계하며, Spring Scheduler를 활용해 통계 생성 작업을 자동화했습니다.

또한 실행 결과의 성공 여부에 따라 이력을 필터링할 수 있는 조회 기능을 추가하고, JUnit 테스트를 통해 주요 서비스 로직을 검증했습니다.

---

## 2. 기술 스택

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate

### Database
- MySQL

### Test
- JUnit 5
- Spring Boot Test

### Tools
- IntelliJ IDEA
- Postman
- Git
- GitHub

---

## 3. 주요 기능

### 모니터링 대상 API 관리
- 모니터링 대상 API 등록
- API 목록 조회
- API 이름, URL, HTTP Method, 활성화 여부 관리

### API 실행 이력 관리
- 실행 성공/실패 여부 저장
- HTTP Status 저장
- 응답시간 저장
- 실패 시 오류 메시지 저장
- 최근 실행 이력 조회

### 실행 이력 필터링
성공 여부에 따라 실행 이력을 조회할 수 있습니다.

```http
GET /api/monitored-apis/{apiId}/executions?success=true
GET /api/monitored-apis/{apiId}/executions?success=false
```

### 일별 통계 집계
- 전체 호출 수
- 성공 건수
- 실패 건수
- 성공률
- 평균 응답시간

### 스케줄러
Spring Scheduler를 이용해 일별 통계를 자동 집계합니다.

---

## 4. 전체 처리 흐름

```text
Postman / Client
        ↓
Controller
        ↓
Service
        ↓
Repository
        ↓
Spring Data JPA
        ↓
MySQL
```

통계 집계 흐름:

```text
API 실행 이력 저장
        ↓
ApiExecutionLog
        ↓
Scheduler
        ↓
JPA 집계 Query
        ↓
DailyApiStat 저장
        ↓
통계 조회 API
```

---

## 5. 프로젝트 구조

```text
src/main/java/com/yoona/apihealthmonitor
├── config
├── controller
│   ├── ExecutionController
│   ├── MonitoredApiController
│   └── StatController
├── dto
├── entity
├── exception
├── repository
│   ├── ApiExecutionLogRepository
│   ├── DailyApiStatRepository
│   ├── ExecutionAggregation
│   └── MonitoredApiRepository
├── scheduler
├── service
└── ApiHealthMonitorApplication
```

`Controller → Service → Repository → Database` 구조로 계층을 분리해 구현했습니다.

---

## 6. DB 구조

주요 테이블은 총 3개입니다.

### monitored_api
모니터링 대상 API 정보

- id
- name
- endpoint_url
- http_method
- active
- created_at
- updated_at

### api_execution_log
API 실행 이력

- id
- monitored_api_id
- success
- http_status
- latency_ms
- error_message
- executed_at

### daily_api_stat
일별 집계 통계

- monitored_api_id
- stat_date
- total_count
- success_count
- failure_count
- success_rate
- average_latency_ms
- calculated_at

---

## 7. 주요 API

### 모니터링 API 등록

```http
POST /api/monitored-apis
```

요청 예시:

```json
{
  "name": "회원 조회 API",
  "endpointUrl": "https://example.com/api/users",
  "httpMethod": "GET",
  "active": true
}
```

### 모니터링 API 목록 조회

```http
GET /api/monitored-apis
```

### 실행 이력 등록

```http
POST /api/monitored-apis/{apiId}/executions
```

성공 요청 예시:

```json
{
  "success": true,
  "httpStatus": 200,
  "latencyMs": 120
}
```

실패 요청 예시:

```json
{
  "success": false,
  "httpStatus": 500,
  "latencyMs": 900,
  "errorMessage": "Database timeout"
}
```

### 전체 실행 이력 조회

```http
GET /api/monitored-apis/{apiId}/executions
```

### 실패 이력만 조회

```http
GET /api/monitored-apis/{apiId}/executions?success=false
```

### 성공 이력만 조회

```http
GET /api/monitored-apis/{apiId}/executions?success=true
```

### 일별 통계 집계

```http
POST /api/monitored-apis/{apiId}/stats/daily
```

### 일별 통계 조회

```http
GET /api/monitored-apis/{apiId}/stats/daily?from=2026-09-01&to=2026-09-30
```

응답 예시:

```json
{
  "totalCount": 2,
  "successCount": 1,
  "failureCount": 1,
  "successRate": 50.0,
  "averageLatencyMs": 510.0
}
```

---

## 8. 테스트

JUnit 기반 서비스 테스트를 작성했습니다.

검증 항목:

- 성공 실행 이력 저장
- 실패 실행 시 오류 메시지 필수 검증
- 실패 실행 이력만 필터링 조회

테스트 결과:

```text
3 tests passed
```

---

## 9. 직접 추가한 기능

기존 실행 이력 조회 기능에서 확장해  
**성공 여부에 따른 실행 이력 필터링 기능**을 추가했습니다.

Repository에 조건 조회 메서드를 추가하고, Service에서 `success` 파라미터 존재 여부에 따라 전체 조회와 조건 조회를 분기하도록 구현했습니다.

```text
GET /executions
→ 전체 실행 이력 조회

GET /executions?success=true
→ 성공 이력만 조회

GET /executions?success=false
→ 실패 이력만 조회
```

해당 기능에 대한 JUnit 테스트도 함께 작성했습니다.

---

## 10. 실행 방법

### 1) MySQL Database 생성

```sql
CREATE DATABASE api_health_monitor
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### 2) 환경 변수 설정

DB 비밀번호는 소스코드에 직접 저장하지 않고 환경 변수로 관리합니다.

```text
DB_PASSWORD=your_mysql_password
```

### 3) Spring Boot 실행

`ApiHealthMonitorApplication`을 실행합니다.

기본 서버 주소:

```text
http://localhost:8080
```

### 4) Postman 테스트

프로젝트 루트의 아래 파일을 Postman에 Import하여 API를 테스트할 수 있습니다.

```text
postman_collection.json
```

---

## 11. 구현하며 경험한 내용

- Spring Boot 기반 REST API 구현
- Controller / Service / Repository 계층 분리
- Spring Data JPA를 활용한 MySQL 연동
- Entity 기반 데이터 모델링
- 실행 이력 저장 및 조건 조회
- JPA 집계 Query 작성
- Spring Scheduler 기반 일별 통계 자동 집계
- Validation 및 예외 처리
- JUnit 기반 서비스 로직 테스트
- Postman을 활용한 REST API 검증
- Git / GitHub 기반 버전 관리

---

## 12. 향후 개선 방향

- 실제 외부 API 주기적 호출 기능
- 장애 발생 임계치 설정
- 이메일 또는 Slack 장애 알림
- Swagger(OpenAPI) 기반 API 문서화
- Docker 기반 실행 환경 구성
- AWS 배포 및 운영 환경 구성
- 통계 조회 성능 개선 및 쿼리 최적화

---

## Author

**이윤아**

Java Backend Developer

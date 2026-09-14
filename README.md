# API Health Monitor

Spring Boot 기반 **API 실행 이력 및 장애 모니터링 서비스**입니다.

등록된 API를 주기적으로 실제 호출하고, HTTP 상태 코드와 응답시간을 측정해 성공/실패 이력을 자동 저장합니다.

저장된 실행 이력을 기반으로 성공률, 실패 건수, 평균 응답시간 등의 일별 통계를 집계하며, Spring Scheduler를 이용해 모니터링과 통계 처리를 자동화했습니다.

---

## 프로젝트 개요

단순 CRUD를 넘어 백엔드 서비스 운영 과정에서 필요한 흐름을 직접 구현하는 것을 목표로 했습니다.

```text
모니터링 대상 API 등록
        ↓
실제 외부 API 호출
        ↓
HTTP Status / 응답시간 측정
        ↓
성공·실패 이력 저장
        ↓
일별 통계 집계
        ↓
통계 조회
```

초기에는 실행 결과를 직접 등록하는 방식으로 구성했으며, 이후 **등록된 API를 실제로 호출해 실행 결과를 자동 수집하는 기능**을 추가해 모니터링 서비스 형태로 확장했습니다.

---

## Tech Stack

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

## 주요 기능

### 1. 모니터링 대상 API 관리

- 모니터링 대상 API 등록
- API 목록 조회
- API 이름 관리
- Endpoint URL 관리
- HTTP Method 관리
- 활성화 여부 관리

---

### 2. 실제 외부 API 모니터링

등록된 Endpoint URL을 실제로 호출하고 다음 정보를 자동으로 측정 및 저장합니다.

- 성공 / 실패 여부
- HTTP Status
- 응답시간
- 오류 메시지
- 실행 시각

성공 응답 예:

```json
{
  "success": true,
  "httpStatus": 200,
  "latencyMs": 190,
  "errorMessage": null
}
```

실패 응답 예:

```json
{
  "success": false,
  "httpStatus": 500,
  "latencyMs": 580,
  "errorMessage": "HTTP status: 500"
}
```

---

### 3. 수동 모니터링 실행

특정 API의 상태를 즉시 확인할 수 있습니다.

```http
POST /api/monitored-apis/{apiId}/check
```

처리 흐름:

```text
POST /check
    ↓
MonitoringController
    ↓
ApiMonitoringService
    ↓
외부 API 실제 호출
    ↓
HTTP Status / latency 측정
    ↓
ExecutionService
    ↓
MySQL 저장
```

---

### 4. Scheduler 기반 자동 모니터링

`active=true` 상태인 API를 Spring Scheduler가 주기적으로 조회하고 자동 호출합니다.

```java
@Scheduled(fixedDelay = 60000)
```

약 60초 간격으로 다음 흐름이 자동 수행됩니다.

```text
활성 API 조회
    ↓
외부 API 자동 호출
    ↓
응답 상태 확인
    ↓
응답시간 측정
    ↓
실행 이력 자동 저장
```

실제 테스트에서 실행 이력이 약 1분 간격으로 지속적으로 저장되는 것을 확인했습니다.

---

### 5. API 실행 이력 관리

API 실행 결과를 MySQL에 저장합니다.

저장 정보:

- 성공 / 실패 여부
- HTTP Status
- 응답시간
- 오류 메시지
- 실행 시각

전체 실행 이력 조회:

```http
GET /api/monitored-apis/{apiId}/executions
```

---

### 6. 성공 / 실패 이력 필터 조회

기존 실행 이력 조회 기능을 확장해 `success` Query Parameter에 따라 실행 결과를 필터링하도록 구현했습니다.

전체 조회:

```http
GET /api/monitored-apis/{apiId}/executions
```

성공 이력만 조회:

```http
GET /api/monitored-apis/{apiId}/executions?success=true
```

실패 이력만 조회:

```http
GET /api/monitored-apis/{apiId}/executions?success=false
```

Repository에는 조건 조회 메서드를 추가했습니다.

```java
findByMonitoredApiIdAndSuccessOrderByExecutedAtDesc(...)
```

Service에서는 `success` 값의 존재 여부에 따라 전체 조회와 조건 조회를 분기하도록 구현했습니다.

---

## 일별 통계

저장된 실행 이력을 기반으로 다음 데이터를 집계합니다.

- 전체 호출 수
- 성공 건수
- 실패 건수
- 성공률
- 평균 응답시간

통계 예:

```json
{
  "totalCount": 2,
  "successCount": 1,
  "failureCount": 1,
  "successRate": 50.0,
  "averageLatencyMs": 510.0
}
```

통계 집계:

```http
POST /api/monitored-apis/{apiId}/stats/daily
```

통계 기간 조회:

```http
GET /api/monitored-apis/{apiId}/stats/daily?from=2026-09-01&to=2026-09-30
```

---

## Architecture

### REST API 처리 구조

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

### 자동 모니터링 흐름

```text
ApiMonitoringScheduler
        ↓
활성 API 조회
        ↓
ApiMonitoringService
        ↓
외부 Endpoint 실제 호출
        ↓
HTTP Status / latency 측정
        ↓
ExecutionService
        ↓
ApiExecutionLog 저장
```

### 통계 처리 흐름

```text
ApiExecutionLog
        ↓
JPA Aggregation Query
        ↓
DailyApiStat
        ↓
통계 조회 API
```

---

## Project Structure

```text
src/main/java/com/yoona/apihealthmonitor
├── config
├── controller
│   ├── ExecutionController
│   ├── MonitoredApiController
│   ├── MonitoringController
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
│   ├── ApiMonitoringScheduler
│   └── DailyStatScheduler
├── service
│   ├── ApiMonitoringService
│   ├── ExecutionService
│   ├── MonitoredApiService
│   └── StatService
└── ApiHealthMonitorApplication
```

---

## Database

주요 테이블은 3개입니다.

### monitored_api

모니터링 대상 API 정보를 관리합니다.

주요 컬럼:

```text
id
name
endpoint_url
http_method
active
created_at
updated_at
```

### api_execution_log

API 실행 이력을 저장합니다.

주요 컬럼:

```text
id
monitored_api_id
success
http_status
latency_ms
error_message
executed_at
```

### daily_api_stat

API별 일별 통계 데이터를 저장합니다.

주요 컬럼:

```text
monitored_api_id
stat_date
total_count
success_count
failure_count
success_rate
average_latency_ms
calculated_at
```

관계:

```text
monitored_api
     │
     ├── 1 : N → api_execution_log
     │
     └── 1 : N → daily_api_stat
```

---

## Validation & Exception Handling

실행 이력 저장 시 잘못된 데이터가 저장되지 않도록 검증 로직을 적용했습니다.

### 성공 실행

성공 실행인데 HTTP Status가 `400 이상`이면 예외 처리합니다.

### 실패 실행

실패 실행에는 `errorMessage`가 반드시 존재하도록 검증합니다.

### 오류 응답 처리

외부 API 호출에서 HTTP 오류가 발생하면 다음 정보를 저장합니다.

- HTTP Status
- 응답시간
- 실패 상태
- 오류 메시지

---

## Test

JUnit 기반 서비스 테스트를 작성했습니다.

검증 항목:

1. 성공 실행 이력 저장
2. 실패 실행 시 오류 메시지 필수 검증
3. 실패 실행 이력 필터 조회

테스트 결과:

```text
3 tests passed
```

---

## 실제 동작 검증

Postman과 MySQL Workbench를 사용해 다음 동작을 직접 확인했습니다.

- Spring Boot 서버 실행
- MySQL 연결
- JPA 테이블 자동 생성
- 모니터링 대상 API 등록
- 실행 이력 저장
- 성공 API 실제 호출
- HTTP 200 성공 판정
- 실패 API 실제 호출
- HTTP 500 실패 판정
- 응답시간 측정
- 오류 메시지 저장
- 성공 / 실패 조건 조회
- Scheduler 기반 자동 호출
- 실행 이력 자동 누적
- 일별 통계 집계
- 통계 조회
- JUnit 테스트 통과

---

## 실제 외부 API 모니터링 테스트

### HTTP 200

테스트 Endpoint:

```text
https://httpbin.org/status/200
```

확인 결과:

```text
success = true
httpStatus = 200
errorMessage = null
```

### HTTP 500

테스트 Endpoint:

```text
https://httpbin.org/status/500
```

확인 결과:

```text
success = false
httpStatus = 500
errorMessage = HTTP status: 500
```

---

## Scheduler 자동 호출 검증

Spring Scheduler를 통해 활성화된 API가 약 60초 간격으로 자동 호출되는 것을 확인했습니다.

실제 성공 API 실행 이력이 다음과 같이 누적되었습니다.

```text
00:53:48
00:54:48
00:55:49
00:56:51
```

이를 통해 다음 전체 흐름이 자동으로 수행되는 것을 확인했습니다.

```text
Scheduler 실행
    ↓
활성 API 조회
    ↓
실제 API 호출
    ↓
HTTP 응답 분석
    ↓
응답시간 측정
    ↓
성공 / 실패 이력 저장
```

---

## 보안 설정

MySQL 비밀번호는 소스코드에 직접 저장하지 않고 환경 변수로 분리했습니다.

`application.yml`

```yaml
password: ${DB_PASSWORD}
```

실행 환경에서는 다음과 같이 설정합니다.

```text
DB_PASSWORD=your_mysql_password
```

실제 DB 비밀번호는 GitHub 저장소에 포함하지 않습니다.

---

## 실행 방법

### 1. MySQL Database 생성

```sql
CREATE DATABASE api_health_monitor
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### 2. 환경 변수 설정

```text
DB_PASSWORD=your_mysql_password
```

### 3. 애플리케이션 실행

IntelliJ에서 다음 클래스를 실행합니다.

```text
ApiHealthMonitorApplication
```

기본 서버 주소:

```text
http://localhost:8080
```

### 4. Postman 테스트

프로젝트 루트의 다음 파일을 Postman에 Import하여 API를 테스트할 수 있습니다.

```text
postman_collection.json
```

---

## 구현하며 경험한 내용

- Spring Boot 기반 REST API 구현
- Controller / Service / Repository 계층 분리
- Spring Data JPA와 MySQL 연동
- Entity 기반 데이터 모델링
- 실제 외부 API HTTP 호출
- HTTP Status 기반 성공 / 실패 판정
- 응답시간 측정
- 실행 이력 자동 저장
- 성공 / 실패 조건 조회
- JPA 집계 Query
- Spring Scheduler 기반 자동 모니터링
- Spring Scheduler 기반 일별 통계 처리
- Validation 및 예외 처리
- JUnit 기반 서비스 테스트
- Postman 기반 REST API 검증
- 환경 변수 기반 민감정보 관리
- Git / GitHub 기반 버전 관리

---

## 향후 개선 방향

- Swagger / OpenAPI 기반 API 문서화
- 장애 발생 임계치 설정
- 연속 장애 감지
- 이메일 / Slack 장애 알림
- API별 모니터링 주기 설정
- Docker 기반 실행 환경 구성
- AWS 배포
- 통계 조회 성능 개선
- Query 최적화
- 모니터링 Dashboard 구현

---

## GitHub

https://github.com/leeya14/api-health-monitor

---

## Author

**이윤아**

Java Backend Developer

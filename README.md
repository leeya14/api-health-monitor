# API Health Monitor

Spring Boot 기반 **API 실행 이력 및 장애 모니터링 서비스**입니다.

단순 게시판 CRUD 대신, 백엔드 서비스 운영에서 자주 마주치는 **실행 이력 저장 → 실패 정보 관리 → 일별 통계 집계 → 조회** 흐름을 구현했습니다.

## 1. 프로젝트 목표

- 모니터링 대상 API 등록/관리
- API 실행 결과(성공/실패, HTTP 상태, 응답시간, 오류 메시지) 저장
- API별 실행 이력 조회
- Spring Scheduler로 전날 통계를 자동 집계
- 호출량/성공률/실패율/평균 응답시간 조회
- Validation 및 공통 예외 처리
- JUnit 기반 서비스 테스트

## 2. 기술 스택

- Java 21
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- Bean Validation
- MySQL 8.x
- H2 (test)
- JUnit 5
- Maven

## 3. 구조

```text
Controller -> Service -> Repository -> MySQL
                 |
                 +-> Scheduler -> Daily Stat
```

패키지 구조:

```text
com.yoona.apihealthmonitor
├── controller
├── dto
├── entity
├── exception
├── repository
├── scheduler
└── service
```

## 4. 실행 전 준비물

### 필수
1. JDK 21
2. IntelliJ IDEA Community 또는 Ultimate
3. MySQL 8.x **또는** Docker Desktop
4. Git

### 선택
- Postman: REST API 직접 호출 확인용
- GitHub 계정: 포트폴리오 공개용

## 5. MySQL 실행

### 방법 A - Docker 사용

```bash
docker compose up -d
```

기본 DB 설정:
- DB: `api_health_monitor`
- username: `root`
- password: `root1234`
- port: `3306`

### 방법 B - 로컬 MySQL 사용

```sql
CREATE DATABASE api_health_monitor
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

환경변수를 지정할 수 있습니다.

```text
DB_URL=jdbc:mysql://localhost:3306/api_health_monitor?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your_password
```

## 6. 애플리케이션 실행

IntelliJ에서 `ApiHealthMonitorApplication`을 실행하거나:

```bash
mvn spring-boot:run
```

기본 포트는 `8080`입니다.

## 7. 가장 먼저 해볼 시나리오

### ① 대상 API 등록

`POST http://localhost:8080/api/monitored-apis`

```json
{
  "name": "회원 조회 API",
  "endpointUrl": "https://example.com/api/users",
  "httpMethod": "GET",
  "active": true
}
```

### ② 성공 실행 이력 2건 등록

`POST http://localhost:8080/api/monitored-apis/1/executions`

```json
{
  "success": true,
  "httpStatus": 200,
  "latencyMs": 120
}
```

```json
{
  "success": true,
  "httpStatus": 200,
  "latencyMs": 180
}
```

### ③ 실패 실행 이력 등록

```json
{
  "success": false,
  "httpStatus": 500,
  "latencyMs": 900,
  "errorMessage": "Database timeout"
}
```

### ④ 오늘 통계 집계

`POST http://localhost:8080/api/monitored-apis/1/stats/daily/2026-09-14/aggregate`

### ⑤ 통계 조회

`GET http://localhost:8080/api/monitored-apis/1/stats/daily?from=2026-09-01&to=2026-09-30`

## 8. 자동 배치

`DailyStatScheduler`가 매일 **00:10 (Asia/Seoul)** 에 전날의 실행 로그를 집계합니다.

집계 지표:
- 전체 호출 수
- 성공 수
- 실패 수
- 성공률
- 평균 응답시간(ms)

## 9. 설계 포인트

### DTO 분리
Entity를 API 요청/응답에 직접 노출하지 않고 DTO를 분리했습니다.

### 계층 분리
Controller / Service / Repository 역할을 분리했습니다.

### 공통 예외 처리
`@RestControllerAdvice`를 사용해 404, 중복, Validation 오류 등을 일정한 JSON 형식으로 반환합니다.

### 집계 쿼리
실행 로그를 애플리케이션 메모리로 모두 읽지 않고 DB 집계 쿼리(`count`, `sum`, `avg`)를 사용합니다.

### 스케줄링
`@Scheduled`를 이용해 전날 통계를 자동으로 생성합니다.

## 10. 테스트

```bash
mvn test
```

포함된 테스트:
- 실행 이력 정상 저장
- 실패 실행 시 오류 메시지 검증
- 일별 호출 수/성공률/평균 응답시간 집계

## 11. 문서

- [API 명세](docs/API.md)
- [ERD](docs/ERD.md)

## 12. 면접에서 설명할 핵심

1. 왜 실행 로그와 일별 통계 테이블을 나눴는가?
   - 실행 이력은 원본 데이터이고, 일별 통계는 반복 집계 비용을 줄이기 위한 요약 데이터입니다.
2. 왜 Controller에서 DB를 바로 조회하지 않았는가?
   - HTTP 처리, 비즈니스 로직, 데이터 접근의 책임을 분리하기 위해 계층을 나눴습니다.
3. 왜 DTO를 사용했는가?
   - API 계약과 DB Entity를 분리해 변경 영향도를 줄이기 위해서입니다.
4. Scheduler가 실패하면?
   - 현재는 오류 로그를 남기고 다음 실행을 기다립니다. 실제 서비스라면 재시도/알림/분산 락 등을 추가할 수 있습니다.
5. 다음 개선점은?
   - 실제 대상 API 자동 호출, 인증/권한, Swagger, Flyway, Docker 이미지화, 모니터링 대시보드 등을 추가할 수 있습니다.

## 13. 이력서 기재 문구 예시

> **API Health Monitor | Spring Boot 기반 API 실행 이력 및 장애 모니터링 서비스**  
> `Java · Spring Boot · Spring Data JPA · MySQL · REST API · Scheduler · JUnit`
>
> - 모니터링 대상 API와 실행 이력을 관리하는 REST API 설계·구현
> - 성공/실패 상태, HTTP Status, 응답시간, 오류 정보를 MySQL에 저장
> - JPA 집계 쿼리와 Spring Scheduler를 활용한 일별 호출량·성공률·평균 응답시간 자동 집계
> - DTO/Service/Repository 계층 분리 및 공통 예외 처리·Validation 적용
> - JUnit으로 실행 이력 저장 및 일별 통계 계산 로직 테스트

**중요:** 실제로 직접 실행하고 코드를 이해한 뒤에 이 문구를 이력서에 넣는 것을 권장합니다.

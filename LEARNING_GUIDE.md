# 이 프로젝트를 '내 프로젝트'로 만들기 위한 학습 순서

아래 순서대로 코드를 읽고 직접 실행하세요.

## 1단계: 30분 - 전체 흐름 보기

1. `ApiHealthMonitorApplication`
2. `MonitoredApiController`
3. `MonitoredApiService`
4. `MonitoredApiRepository`
5. `MonitoredApi`

질문에 답할 수 있어야 합니다.
- Controller는 무엇을 하나?
- Service는 왜 필요한가?
- Repository가 SQL 없이 저장/조회를 할 수 있는 이유는?
- Entity와 DTO의 차이는?

## 2단계: 1시간 - CRUD 직접 호출

Postman에서 다음을 직접 호출합니다.
- POST API 등록
- GET 목록
- PUT 수정
- DELETE 삭제

각 요청 전후로 MySQL 테이블 값이 어떻게 바뀌는지 확인합니다.

## 3단계: 1시간 - 실행 이력 이해

`ExecutionService`를 읽고 성공/실패 실행을 직접 저장합니다.

확인할 것:
- `@ManyToOne`이 의미하는 관계
- 실패인데 errorMessage가 없으면 왜 400이 되는지
- `@Valid`가 언제 동작하는지

## 4단계: 1~2시간 - 통계와 쿼리

`ApiExecutionLogRepository.aggregate()` 쿼리를 읽습니다.

직접 계산해보세요.
- 성공 2건, 실패 1건 -> 성공률 66.67%
- latency 100, 200, 300 -> 평균 200ms

그 후 수동 집계 API를 호출하고 DB의 `daily_api_stat`을 확인합니다.

## 5단계: 30분 - Scheduler

`DailyStatScheduler`의 cron 표현식을 확인합니다.

연습으로 cron을 `0 */1 * * * *`로 잠깐 바꾸어 1분마다 실행되는 로그를 확인한 뒤 다시 원래 값으로 돌려놓습니다.

## 6단계: 1시간 - 테스트

`ExecutionServiceTest`, `DailyStatServiceTest`를 한 줄씩 읽고 실행합니다.

테스트 하나를 직접 추가하세요.
추천: 성공 실행인데 HTTP 500을 넣으면 예외가 발생하는 테스트.

## 7단계: 1~2시간 - 반드시 직접 수정할 기능

아래 중 하나를 직접 추가하면 프로젝트 이해도가 훨씬 올라갑니다.

### 추천 A: 실패 이력만 조회
`GET /api/monitored-apis/{apiId}/executions?success=false`

### 추천 B: 통계 최소/최대 응답시간 추가
DailyApiStat에 min/max latency를 추가합니다.

### 추천 C: API 활성/비활성 필터
`GET /api/monitored-apis?active=true`

면접에서 "AI가 만들어준 프로젝트"가 아니라 본인이 구조를 이해하고 수정한 프로젝트로 설명하려면 이 단계가 중요합니다.

## 8단계: GitHub

추천 commit 순서:

1. `chore: initialize Spring Boot project`
2. `feat: add monitored API CRUD`
3. `feat: add execution history recording`
4. `feat: add daily statistics aggregation`
5. `feat: add daily statistics scheduler`
6. `feat: add validation and global exception handling`
7. `test: add service tests`
8. `docs: add API specification and ERD`

한 번에 ZIP 전체를 커밋하기보다는 기능별로 직접 commit하는 것이 학습과 포트폴리오 모두에 더 좋습니다.

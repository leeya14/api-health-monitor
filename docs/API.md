# API 명세 요약

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/monitored-apis` | 모니터링 대상 API 등록 |
| GET | `/api/monitored-apis` | 전체 대상 조회 |
| GET | `/api/monitored-apis/{id}` | 단건 조회 |
| PUT | `/api/monitored-apis/{id}` | 수정 |
| DELETE | `/api/monitored-apis/{id}` | 삭제 |
| POST | `/api/monitored-apis/{apiId}/executions` | 실행 결과 기록 |
| GET | `/api/monitored-apis/{apiId}/executions?page=0&size=20` | 실행 이력 조회 |
| POST | `/api/monitored-apis/{apiId}/stats/daily/{date}/aggregate` | 특정 API/날짜 통계 수동 집계 |
| POST | `/api/admin/stats/daily/{date}/aggregate` | 전체 API 통계 수동 집계 |
| GET | `/api/monitored-apis/{apiId}/stats/daily?from=2026-09-01&to=2026-09-30` | 일별 통계 조회 |

## 예시: API 등록

```json
{
  "name": "회원 조회 API",
  "endpointUrl": "https://example.com/api/users",
  "httpMethod": "GET",
  "active": true
}
```

## 예시: 성공 실행 기록

```json
{
  "success": true,
  "httpStatus": 200,
  "latencyMs": 142
}
```

## 예시: 실패 실행 기록

```json
{
  "success": false,
  "httpStatus": 500,
  "latencyMs": 918,
  "errorMessage": "Database connection timeout"
}
```


# 🤝 GoodDayTaxi 팀 협업 규칙 (Contributing Guide)

본 문서는 GoodDayTaxi 프로젝트의 **브랜치 전략, Issue / PR 규칙, 코드 리뷰 규칙, DDD & MSA 협업 규칙**을 정의합니다.  
팀원 전원은 반드시 아래 규칙을 준수하여 일관된 코드 품질과 효율적 협업을 유지합니다.

---

# 🏷️ 1. 브랜치 전략 (Branch Strategy)

## 📌 기본 원칙
- 모든 작업은 **Issue 생성 → 이슈번호 기반 브랜치 생성** 순서로 진행합니다.
- 브랜치 네이밍 규칙:

```
feat/account/#1
fix/dispatch/#12
refactor/payment/#8
docs/server/#5

```

## 📁 브랜치 종류
| Prefix | 설명 |
|--------|-------|
| `feat/` | 새로운 기능 개발 |
| `fix/` | 버그 수정 |
| `refactor/` | 리팩토링 |
| `docs/` | 문서 작성 |
| `chore/` | 설정/환경/빌드 |
| `test/` | 테스트 코드 |

---

# 🗂️ 2. 이슈 규칙 (Issue Rules)

- 브랜치를 만들기 전 **반드시 Issue 먼저 생성**합니다.
- 생성 시 필수 기입:
  - 🏷️ Label  
  - 🗃️ Type  
  - 👤 Assignees  
- 외부인도 이해할 수 있도록 **아이콘 기반 분류 시스템 유지**
- PR에는 반드시 `Closes #이슈번호` 추가 (자동 종료)

---

# 🔀 3. Pull Request 규칙 (PR Rules)

## 📌 PR 단위 규칙
- PR은 **최소 단위로 작성**합니다.  
  → 하나의 PR = 하나의 목적  
- 아래 항목 **절대 섞지 않기**
  - 기능 + 리팩토링 + 버그 수정
- 추천 변경량:
  - 🔸 100줄 이상 ❌  
  - 🔸 파일 10개 이상 ❌  

## 📌 PR 생성 규칙
- ✔ Reviewer는 **본인을 제외한 모든 팀원**
- ✔ 리뷰어 최소 **2명 승인 필수**
- ✔ Assignees · Labels 필수 지정
- ✔ PR 생성 후 Slack으로 알리기
- ✔ 내부 API / 외부 API 스펙 변경 또는 Kafka 이벤트 스키마 변경 시  
  → PR 본문에 반드시 상세 기술 + 팀원 DM으로 공유

## 📌 PR Merge 규칙
- 머지 방식: **Squash and Merge**
- 머지 후:
  - 브랜치 자동 삭제
  - 연결된 Issue 자동 종료

---

# 👀 4. 코드 리뷰 규칙 (Code Review Rules)

## 리뷰 포커스(중요한 것 위주)
- DDD Layer 위반 여부
- Aggregate 경계 침범 여부
- Cross-service import 금지 여부
- 설계적 문제, 부적절한 예외처리, 부정합 구조 등

## 리뷰 태도
- 불필요한 코드 스타일 지적 지양
- “왜 이렇게 구현하셨나요?” → 의도를 묻는 방식 선호
- PR 생성 후 **최대 24시간 내 리뷰 완료**

---

# ⚠️ 5. Conflict 해결 규칙
- 충돌 시 **혼자 해결하지 않기**
- 구조적 충돌은 반드시 팀원과 논의
- 어려운 충돌은 즉시 공유 후 함께 해결

---

# ⚙️ 6. CI / 자동화 규칙

- 모든 PR은 CI(Gradle build + Test)를 통과해야 합니다.
- GitHub Actions에서 다음을 자동 실행:
  - `./gradlew clean build`
  - 테스트 실행
- 빌드 실패 → PR 머지 불가

---

# 🔌 7. DDD & MSA 협업 규칙

## 서비스 경계 규칙
- 각 서비스는 **자신의 Bounded Context만 책임진다.**
- 다른 서비스의 엔티티/도메인을 절대 import 하지 않는다.

## 구조 규칙
- Layer 구조 유지:
```

Controller → Service → Port → Adapter

````
- DTO / Domain / Persistence 명확히 분리

## 계약(Contract) 규칙
- API 스펙 변경 시:
  - Issue 생성 → 팀 공유 → PR 설명에 기록
- 이벤트 스키마 변경 시:
  - 반드시 팀 전체 사전 공유

---

# 🔵 8. API 응답 규칙 (Response Rules)

## 🔹 외부 API (External API)
> Gateway, Swagger, Postman 등 클라이언트에서 호출되는 모든 API는  
> **ApiResponse<T>로 래핑하여 응답합니다.**

예)
```java
return ApiResponse.success(userDto);
````

## 🔸 내부 API (Internal API)

> 서비스 간 통신(Feign / REST)은
> **DTO 자체만 응답 (ApiResponse로 감싸지 않음)**

예)

```java
return new DriverStatusResponse(driverId, isOnline);
```

### 요약

| 구분              | 규칙                  | 이유                         |
| --------------- | ------------------- | -------------------------- |
| 🔵 External API | `ApiResponse<T>` 사용 | 클라이언트 응답 구조 통일             |
| 🟠 Internal API | DTO 직접 반환           | MSA 서비스 간의 명확하고 경량화된 계약 유지 |

---

# 🙈 9. Git Commit 아이콘 규칙 (선택 사항)

| 아이콘         | 의미               |
| ----------- | ---------------- |
| 🎉 add      | 신규 파일/초기 세팅      |
| ✨ update    | 기존 파일에 새로운 기능 추가 |
| ♻️ refactor | 리팩토링             |
| 🐛 bugfix   | 버그 수정            |
| 🔥 del      | 파일/기능 삭제         |
| 🚚 move     | 파일 이동            |
| 🍻 test     | 테스트 코드           |
| 🙈 gitfix   | gitignore 수정     |
| 🔨 script   | npm/script 변경    |

---

# 📌 끝으로

이 문서는 GoodDayTaxi 팀의 **공식 협업 기준**이며,
모든 팀원은 PR/Issue/코드 작성 시 본 문서를 준수해야 합니다.

필요 시 언제든지 팀 합의로 업데이트 가능합니다.

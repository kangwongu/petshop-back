# Claude Guidelines

이 파일은 Claude Code (claude.ai/code)가 이 저장소에서 작업할 때 참고하는 **핵심 가이드 문서**입니다.
자세한 규칙은 `.claude/rules/` 하위 문서를 참조하세요: `architecture.md`, `naming.md`, `patterns.md`, `infra.md`

---

## TL;DR (항상 우선 적용)

- `architecture.md`, `naming.md`, `patterns.md`, `infra.md` 문서는 단일 진실 소스다. 충돌 시 해당 문서를 우선한다.
  - `architecture.md` — 패키지 구조, 계층 의존성
  - `naming.md` — 명명 규칙 (파일명, 메서드명, 변수명, Import)
  - `patterns.md` — 헥사고날 아키텍처, 객체 모델링, Repository/Service/DTO 패턴
  - `infra.md` — 인프라 및 외부 통합 (Persistence, 설정·시크릿)
- 작업 전 반드시 **사전 체크리스트**를 제시하고, 사용자의 **명시적 승인 이후**에만 코드를 수정한다.
- 문서에 없는 규칙은 추측하지 않는다. 모호하면 반드시 질문한다.
- 작은 변경을 우선하며, 요청 없는 광범위한 리팩터링은 하지 않는다.
- 트랜잭션 경계, 모듈 경계, public API는 사전 확인 없이 변경하지 않는다.
- 출력은 변경된 파일만, diff 우선, 설명은 5줄 이내로 제한한다.

---

## 기본 지침

- 답변은 한글로 작성한다.
- 멋대로 추측하지 않는다.
- 모르는 것은 반드시 질문한다.

---

## 프로젝트 컨텍스트

- **프로젝트명**: petshop-back — 뚱이요미 샵(굿즈 쇼핑몰) 백엔드 API 서버
- **목적**: 상품 조회 → 주문 생성 → Toss Payments 결제로 이어지는 커머스 백엔드 구현 + 배포·RDS 운영 등 인프라 구성 실습
- **해결하는 문제**: 헥사고날 아키텍처 기반으로 단순하고 유지보수하기 쉬운 커머스 백엔드를 구현하면서, 실제 운영 환경 배포 경험을 쌓는다
- **기술 스택**: Java 25 / Spring Boot 4.1.1 / JPA / MySQL (운영은 AWS RDS)
- **아키텍처**: Hexagonal (Ports & Adapters) — 세부 패키지 컨벤션은 아직 미확정 상태이며 우선 표준 골격으로 시작 중
- **주요 도메인**: `product`, `order`, `payment`
- **패키지 루트**: `com.ddungyomi.petshop`

### 핵심 제약조건

- `domain` 계층은 프레임워크/JPA 독립성을 유지한다.
- `application/service`는 `port.in/out`에만 의존하며, 어댑터 구현체에 직접 의존하지 않는다.
- 트랜잭션 경계는 `application/service`에 위치한다.
- DTO(req/res)는 Controller 경계, Command는 Application 경계, 영속 Entity는 `adapter.out.persistence` 경계에 둔다.
- 세부 패키지 규칙은 `architecture.md`를 따른다.
- 월 운영비 $5 이하, 유지보수 인원 1명 제약 하에 **과설계를 지양**하고 단순한 구조를 우선한다.

---

## 명령어

```bash
# 빌드 (테스트 제외)
./gradlew clean build -xtest

# 빌드 (테스트 포함)
./gradlew clean build

# 특정 테스트 클래스 실행
./gradlew test --tests "com.ddungyomi.petshop.domain.product.application.service.ProductWriteServiceTest"

# 전체 테스트 실행
./gradlew test
```

---

## 주요 기술 스택

- **Java 25** (LTS), **Spring Boot 4.1.1**
- **JPA** (Hibernate, Spring Boot 관리 버전) — MySQL(`mysql-connector-j`) 사용, 로컬 편의용 H2 런타임 포함
- **Spring Validation** (`spring-boot-starter-validation`)
- **SpringDoc OpenAPI 3.1.0** — Swagger UI (`/swagger-ui.html`)
- **Lombok**, **jackson-datatype-jsr310**
- **Toss Payments** — 별도 SDK 없이 Spring RestClient로 직접 연동 (Phase 3에서 구현 예정, 아직 의존성 미추가)
- **AWS RDS(MySQL) / Secrets Manager** — 운영 전용, `spring-cloud-aws-starter-secrets-manager`는 Phase 5에서 추가 예정

> QueryDSL, Redis, OpenSearch, AWS SQS/S3 등은 현재 사용하지 않는다. 필요해지면 그때 의존성을 추가하고 이 문서를 갱신한다.

---

## 도메인 모듈

`product`(상품/카테고리), `order`(주문), `payment`(Toss 결제) — 의존 순서: product → order → payment

공통 패키지(`config/`, `common/exception/`)는 아직 생성되지 않은 상태이며, `docs/phase0-todo.md`의 Phase 0 작업에서 세팅될 예정이다.

---

## 작업 시작 전 필수 절차

- 작업 전 반드시 아래 체크리스트를 제시하고 **명시적 승인 이후**에만 코드를 수정한다.
- **특히, 어떤 구체적인 작업을 수행할 것인지(예: TODO 리스트의 특정 항목, 체크박스 번호 등)를 명확히 제시하고 사용자의 확인을 받은 후 작업을 시작해야 한다.**

### 체크리스트

1. **목적**: 무엇을, 왜 바꾸는가?
2. **변경 범위**: 수정 대상 파일/클래스/메서드
3. **영향 범위**: 트랜잭션/아키텍처 경계 침범 여부, 연관 도메인
4. **불확실한 부분**: 가정하고 있는 전제, 확인 필요 사항

---

## 메서드 명명 규칙 (필수 준수)

Port/UseCase/Service 메서드 작성 전 반드시 `.claude/rules/naming.md`의 메소드명 규칙을 확인한다.

---

## API Path 컨벤션

- **TR-06 제약이 최우선**: 이미 구현된 프론트엔드(`petshop-frontend-prev`)가 기대하는 엔드포인트/요청·응답 포맷을 그대로 준수해야 하며, 임의로 경로나 포맷을 바꾸지 않는다.
- 프론트엔드가 기대하는 경로가 이 저장소에 문서화되어 있지 않다면, 추측해서 만들지 말고 먼저 질문한다.
- 프론트엔드 계약이 없는 완전히 새로운 엔드포인트(예: 시딩 API)를 추가할 때만 아래 기본 규칙을 적용한다.
  - 복수형 명사 사용
  - 두 단어 이상은 `-`(하이픈) 연결
  - 목록 조회 시 path에서 `/list` 제거
- 아직 운영 트래픽이 없는 초기 단계이므로, 기존 경로를 유지한 채 신규 경로를 배열로 추가하는 하위 호환 처리는 해당하지 않는다.

---

## 아키텍처 규칙

- 아키텍처 관련 판단은 반드시 `architecture.md`를 읽고 결정한다.
- 변경 사항 적용 전 각 계층(UseCase, Port, Service 등)의 명명 규칙을 철저히 확인한다.
- 아키텍처 위반 가능성이 있다면 작업을 중단하고 먼저 경고한다.

---

## 코드 작성 규칙

- `naming.md`, `patterns.md`를 따른다.
- 컴파일만 되는 코드가 아니라 '읽히는 코드'를 작성한다.
- 정상 동작하는 코드는 다시 작성하지 않는다.
- 작성 전 자기검증: "시니어 엔지니어가 보면 과도하다고 할 것인가?" — Yes라면 단순화한다.
- 요청하지 않은 추상화, 유연성, 설정 포인트를 추가하지 않는다.

---

## 디버깅 규칙

- 에러 분석 시 가장 가능성 높은 원인부터 식별한다.
- 문제를 해결하는 최소 변경을 우선 제안한다.

---

## 금지 사항

- 근거 없는 성능 수치 제시.
- 문서에 없는 규칙을 사실처럼 단정.

# Implementation Plan

이 문서는 `constitution.md`(목적/성공 기준/제약사항)와 `specification.md`(요구사항, FR/NFR/TR ID)를 바탕으로 실제 구현 순서와 방식을 정의한다.

## 구현 원칙

- 진행 상황은 `specification.md`의 요구사항 ID(FR/NFR/TR)를 기준으로 추적한다.
- 도메인 간 의존 순서대로 구현한다: **Product/Category → Order → Payment** (Order가 Product를 참조, Payment가 Order를 참조).
- 각 도메인은 도메인 모델 → 포트 → 서비스 → 어댑터/컨트롤러까지 한 번에 이어지는 "동작하는 세로 슬라이스"로 완성한 뒤 다음 도메인으로 넘어간다.
- 헥사고날 아키텍처 세부 컨벤션(TR-05)은 아직 문서로 확정되지 않았으므로, 우선 표준 골격으로 시작하고 문서가 도착하면 구조를 재정렬한다.
- 각 Phase는 이전 Phase가 동작 확인된 뒤에만 다음으로 넘어간다 (순차 진행).

## Phase 0. 프로젝트 초기 설정

- Gradle 프로젝트 스캐폴딩 (Java 25, Spring Boot 4.x) — TR-01, TR-03
- 헥사고날 표준 패키지 골격 세팅 — TR-05
- 로컬 MySQL 연결 및 `application.yml`/`application-local.yml`/`application-prod.yml` 프로파일 구성 — TR-02, TR-09
- 기동 확인용 헬스체크 엔드포인트 구현
- Swagger(springdoc-openapi) 연동 및 `/swagger-ui.html` 노출 — TR-08 (이후 도메인의 컨트롤러는 구현과 동시에 Swagger 문서에 노출)

## Phase 1. 상품/카테고리 (Product/Category)

- 도메인 모델: `Category`, `Product`
- 상품 목록 조회(카테고리 필터링) / 상세 조회 API — FR-01, FR-02
- 초기 데이터 시딩 API — FR-06 (Product/Category 스키마 확정 후 구현)

**완료 기준**: 시딩 API 호출 후 목록/상세 API가 실제 데이터를 반환한다.

## Phase 2. 주문 (Order)

- 도메인 모델: `Order`, `OrderItem`
- 주문 생성 API — FR-03 (buyer 정보 + 상품 항목 → 상품 현재가 기준 총액 계산, 가격 스냅샷 보관)
- Phase 1의 상품 조회 로직에 의존하므로 그 이후 진행

**완료 기준**: 시딩된 상품으로 주문을 생성하면 올바른 총액과 상태(PENDING)로 저장된다.

## Phase 3. 결제 (Payment / Toss 연동)

- 도메인 모델: `Payment`
- 결제 승인(confirm) API — FR-04: 클라이언트 금액과 주문 총액 대조(NFR-01) → Toss RestClient 호출 → 주문/결제 상태를 한 트랜잭션에서 갱신
- 웹훅 수신 API — FR-05: `paymentKey` 기준 멱등 처리(NFR-02)
- Phase 2의 Order에 의존하므로 그 이후 진행

**완료 기준**: Toss 테스트 키로 결제 승인 플로우가 성공/실패 양쪽 다 정상 처리된다.

## Phase 4. 프론트엔드 연동 검증

- 기존 `petshop-frontend-prev`의 `NEXT_PUBLIC_API_URL`을 로컬 백엔드로 지정
- API 계약(TR-06) 준수 여부를 실제 화면 클릭으로 검증: 홈 → 상품상세 → 장바구니 → 주문서 → Toss 결제 팝업 → 결제 완료
- 응답 형식이 프론트 기대와 어긋나는 부분이 있으면 이 단계에서 수정

**완료 기준**: 전체 구매 플로우가 로컬 환경에서 프론트 화면으로 끊김 없이 동작한다.

## Phase 5. 배포 및 인프라 구성

- AWS RDS(MySQL) 구성 — 비용 제약(NFR-03, TR-07)에 맞는 최소 사양 선택
- AWS Secrets Manager에 운영 시크릿 등록 (NFR-05, TR-09) — DB 자격증명 + Toss 시크릿 키를 하나의 JSON 시크릿으로 통합해 비용 최소화
- `spring-cloud-aws-starter-secrets-manager` 의존성 추가 및 `application-prod.yml`에 `spring.config.import=aws-secretsmanager:...` 설정
- 배포 환경(EC2/ECS 등)에 Secrets Manager 읽기 권한을 가진 IAM 역할 부여
- 애플리케이션 배포 (구체 호스팅 방식은 비용 제약 하에서 별도 결정 필요 — TBD)
- 배포 환경에서 시딩 API(FR-06) 호출로 초기 데이터 적재
- 배포 환경에서 Phase 4와 동일한 전체 플로우 재검증

**완료 기준**: `constitution.md`의 성공 기준(배포 환경에서 전체 플로우 동작, 주문을 받을 수 있는 상태) 충족.

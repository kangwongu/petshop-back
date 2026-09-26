# Specification

각 요구사항은 고유 ID를 부여해 추적한다 (커밋/PR/코드 주석 등에서 ID로 참조 가능).

## 기능 요구사항 (Functional Requirements)

| ID | 요구사항 | 설명 |
|---|---|---|
| FR-01 | 상품 목록 조회 | 카테고리 필터링을 지원하는 상품 목록 조회 |
| FR-02 | 상품 상세 조회 | 단일 상품의 상세 정보 조회 |
| FR-03 | 주문 생성 | 비회원(게스트) buyer 정보(이름/연락처/주소) + 상품 항목 리스트를 받아 주문 생성. 총액은 상품의 현재 가격 기준으로 계산하고, 주문 시점 가격을 스냅샷으로 보관 |
| FR-04 | 결제 승인 (Toss confirm) | 클라이언트 요청 금액과 서버 측 주문 총액을 대조한 뒤 Toss 결제 승인 진행 |
| FR-05 | 결제 웹훅 수신 | Toss로부터의 결제 상태 알림을 멱등하게 처리 |
| FR-06 | 초기 데이터 시딩 | 카테고리/상품 초기 데이터를 채우는 API 제공 |

### 범위 제외 (Out of Scope)

- 회원가입/로그인 등 인증
- 장바구니 서버 측 저장
- 재고 관리
- 결제 취소/환불
- 관리자 기능
- 카테고리 목록 조회 API (프론트엔드에서 하드코딩된 값 사용)

## 비기능 요구사항 (Non-functional Requirements)

| ID | 요구사항 | 설명 |
|---|---|---|
| NFR-01 | 결제 정합성 | 결제 승인 전 클라이언트가 보낸 금액과 서버의 주문 총액이 반드시 일치해야 하며, 불일치 시 Toss 승인 API를 호출하지 않는다 |
| NFR-02 | 웹훅 멱등성 | 동일한 결제 건에 대한 중복 웹훅 수신 시에도 상태가 중복 반영되지 않는다 |
| NFR-03 | 비용 효율성 | 월 운영 비용 $5 이하를 유지할 수 있는 인프라/리소스 사이징을 전제로 한다 |
| NFR-04 | 유지보수 용이성 | 유지보수 인원 1명이 무리 없이 이해·수정할 수 있는 단순한 구조를 유지하며, 과설계를 지양한다 |
| NFR-05 | 민감정보 관리 | 결제 시크릿 키, DB 자격증명 등 민감정보는 코드에 포함하지 않는다. 운영 환경은 AWS Secrets Manager로 관리하며, `application-local.yml`/`application-prod.yml`은 실제 시크릿 값을 담지 않으므로 git으로 관리한다 |

## 기술 요구사항 및 제약사항 (Technical Requirements & Constraints)

| ID | 요구사항 | 설명 |
|---|---|---|
| TR-01 | 언어/프레임워크 | Java 25 (LTS), Spring Boot 4.x (최신 안정 버전) |
| TR-02 | DB | MySQL — 로컬/운영 공통, 운영 환경은 AWS RDS |
| TR-03 | 빌드 도구 | Gradle |
| TR-04 | 결제 연동 | Toss Payments, 별도 SDK 없이 Spring RestClient로 직접 연동 |
| TR-05 | 아키텍처 | Hexagonal (Ports & Adapters) — 세부 컨벤션은 별도 문서로 추후 제공 예정 |
| TR-06 | API 계약 고정 | 이미 구현된 프론트엔드(petshop-frontend-prev)가 기대하는 엔드포인트/요청·응답 형식을 그대로 준수해야 하며 임의 변경 불가 |
| TR-07 | 인프라 비용 | 월 $5 이하 제약에 맞는 저비용 구성(RDS 최소 사양 등) 선택 필요. AWS Secrets Manager 비용(시크릿당 월 $0.40 + API 호출 비용)도 이 예산에 포함되므로, 시크릿 개수를 최소화한다(예: DB 자격증명 + Toss 키를 하나의 JSON 시크릿으로 통합) |
| TR-08 | API 명세서 | Swagger(springdoc-openapi)로 API 문서를 자동 생성하고 `/swagger-ui.html`로 제공 |
| TR-09 | 민감정보 연동 방식 | `application-local.yml`(로컬) / `application-prod.yml`(운영) 프로파일로 설정을 분리한다. 운영 프로파일에서는 `spring-cloud-aws-starter-secrets-manager`로 AWS Secrets Manager의 시크릿을 Spring `PropertySource`로 자동 주입한다 |

## UI/UX

이 저장소는 백엔드 전용 프로젝트이므로 UI/UX 요구사항은 포함하지 않는다. UI/UX는 별도 프론트엔드 프로젝트에서 관리한다.

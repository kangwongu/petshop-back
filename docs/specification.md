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
- 재고 관리 *(동시성 학습 목적의 향후 TODO는 TR-15 참고)*
- 결제 취소/환불
- 관리자 기능
- 카테고리 목록 조회 API (프론트엔드에서 하드코딩된 값 사용)

## 비기능 요구사항 (Non-functional Requirements)

| ID | 요구사항 | 설명 |
|---|---|---|
| NFR-01 | 결제 정합성 | 결제 승인 전 클라이언트가 보낸 금액과 서버의 주문 총액이 반드시 일치해야 하며, 불일치 시 Toss 승인 API를 호출하지 않는다 |
| NFR-02 | 웹훅 멱등성 | 동일한 결제 건에 대한 중복 웹훅 수신 시에도 상태가 중복 반영되지 않는다 |
| NFR-03 | 비용 효율성 | 월 운영 비용 $10 이하를 유지할 수 있는 인프라/리소스 사이징을 전제로 한다 |
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
| TR-07 | 인프라 비용 | 월 $10 이하 제약에 맞는 저비용 구성(RDS 최소 사양 등) 선택 필요. AWS Secrets Manager 비용(시크릿당 월 $0.40 + API 호출 비용)도 이 예산에 포함되므로, 시크릿 개수를 최소화한다(예: DB 자격증명 + Toss 키를 하나의 JSON 시크릿으로 통합) |
| TR-08 | API 명세서 | Swagger(springdoc-openapi)로 API 문서를 자동 생성하고 `/swagger-ui.html`로 제공 |
| TR-09 | 민감정보 연동 방식 | `application-local.yml`(로컬) / `application-prod.yml`(운영) 프로파일로 설정을 분리한다. 운영 프로파일에서는 `spring-cloud-aws-starter-secrets-manager`로 AWS Secrets Manager의 시크릿을 Spring `PropertySource`로 자동 주입한다 |
| TR-10 | 미디어 저장 | 상품 이미지는 AWS S3에 저장한다. 운영자가 최초 상품 등록 시 콘솔/CLI로 1회성 수동 업로드하며, 업로드/관리를 위한 API는 제공하지 않는다. 이미지 URL은 상품 테이블에 직접 기록한다 |
| TR-11 | 배포 방식 | 컨테이너 방식으로 배포한다. 1단계는 AWS ECR에 이미지를 push하고 EC2 단일 인스턴스에서 Docker로 pull & 실행하는 구성이다. ECS/EKS로의 확장은 향후 트래픽/예산에 따라 별도 검토하며 현재는 착수하지 않는다 |
| TR-12 | 로깅/모니터링 | Spring Boot 표준 Logback 콘솔(stdout) 출력을 사용하며, 컨테이너 실행 시 `--log-driver=awslogs`로 CloudWatch Logs에 전송한다. 로그 조회는 CloudWatch Logs Insights를 사용하고, 예외도 표준 로그로 남기므로 별도 에러 추적 도구(Sentry 등)는 사용하지 않는다. ELK 스택 전환은 추후 TODO로 남긴다 |
| TR-13 | CI/CD | GitHub Actions로 구성한다. 별도 서버(Jenkins 등) 없이 리포지토리에 붙는 방식으로, 빌드 → 컨테이너 이미지 ECR push → EC2 배포까지의 파이프라인을 목표로 한다 |
| TR-14 | 결제 정합성 보강 (TODO) | Toss 웹훅은 처리 실패 시 재시도되지만(FR-05), 재시도가 모두 실패하면 결제 상태가 영구적으로 불일치할 수 있다. `paymentKey`/`orderId` 기준 능동적 재조회(reconciliation) 배치로 보강하는 방안을 추후 검토하며, 현재는 착수하지 않는다 |
| TR-15 | 재고 도메인 및 동시성 제어 (TODO) | 인기 상품 동시 주문 시 재고 경합(overselling)을 다루는 동시성 제어 학습을 위해, 재고(Inventory) 도메인 추가와 동시성 제어(낙관적 락/비관적 락/조건부 UPDATE 등) 적용을 향후 별도로 검토한다. 현재는 범위 제외 상태이며 착수하지 않는다 |
| TR-16 | 네트워크/배포 단계적 고도화 (TODO) | Phase 5의 1단계 구성(NAT Gateway 미사용, ELB 미사용, EC2 public subnet+ECR)은 비용 제약과 컨테이너 배포 학습을 위한 시작점이다. 초기 구성 완료 후 다음 순서로 실무형 구성을 단계적으로 체험할 계획이다: (1) NAT Gateway 추가 + EC2를 private subnet으로 이동하고 Bastion Host를 통해 접근하는 구성, (2) ALB(ELB) 도입, (3) EC2+ECR → ECS로의 마이그레이션. 각 단계는 NFR-03(월 $10 이하) 제약을 일시적으로 초과하는 학습용 실습으로 진행하며, 현재는 범위 제외 상태이고 착수하지 않는다 |

## UI/UX

이 저장소는 백엔드 전용 프로젝트이므로 UI/UX 요구사항은 포함하지 않는다. UI/UX는 별도 프론트엔드 프로젝트에서 관리한다.

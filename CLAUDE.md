# CLAUDE.md

파일 = Claude Code (claude.ai/code) 저장소 작업 가이드.

@.claude/rules/architecture.md
@.claude/rules/commit.md
@.claude/rules/guidelines.md
@.claude/rules/naming.md
@.claude/rules/patterns.md
@.claude/rules/infra.md



---
## 핵심 규칙

**.claude/rules/ 하위 문서 = 단일 진실 소스. 충돌 시 해당 문서 우선:**

- `guidelines.md` — 작업 전 체크리스트, 메서드 명명, API Path, 에이전트 파이프라인
- `architecture.md` — 패키지 구조, 계층 의존성
- `naming.md` — Java 코딩 스타일, 명명 규칙
- `patterns.md` — Repository, Service, Mapper, DTO, Builder 패턴
- `infra.md` — Persistence, Auth, 설정·시크릿


---

## 기본 원칙

1. **사전 체크리스트 필수**: 코드 수정 전 목적·변경범위·영향범위·불확실부분 제시 + 명시 승인
2. **문서 우선**: 문서 없는 규칙 = 추측 금지, 질문
3. **최소 변경**: 미요청 리팩터링 금지
4. **경계 보호**: 트랜잭션·모듈·public API 경계 = 사전 확인 없이 변경 금지

---

## 빌드 및 테스트

```bash
./gradlew clean build -xtest     # 테스트 제외
./gradlew clean build            # 테스트 포함
./gradlew test                   # 전체 테스트
./gradlew test --tests "패키지명.클래스명"  # 특정 테스트
```

---

## 프로젝트 정보

- **프로젝트**: petshop-back — 뚱이요미 샵(굿즈 쇼핑몰) 백엔드 API 서버. 프론트엔드는 별도 저장소(`petshop-frontend-prev`)에서 관리
- **목적**: 상품 조회 → 주문 생성 → Toss Payments 결제로 이어지는 커머스 백엔드 구현 + 배포·RDS 운영 등 인프라 구성 실습
- **패키지 루트**: `com.ddungyomi.petshop`
- **도메인**: `product`(상품/카테고리), `order`(주문), `payment`(Toss 결제) — 의존 순서: product → order → payment
- **스택**: Java 25, Spring Boot 4.1.1, JPA, MySQL(로컬/운영 공통, 운영은 AWS RDS), Toss Payments(별도 SDK 없이 Spring RestClient로 직접 연동)
- **아키텍처**: Hexagonal (Ports & Adapters) — 세부 패키지 컨벤션은 아직 미확정 상태이며 우선 표준 골격으로 시작 중 (`docs/plan.md` 참고)
- **인증**: 없음 — 비회원(게스트) 체크아웃만 지원
- **제약**: 월 운영비 $5 이하, 유지보수 인원 1명 → 과설계 지양, 단순한 구조 우선
- **범위 제외**: 회원가입/로그인, 장바구니 서버 측 저장, 재고 관리, 결제 취소/환불, 관리자 기능
- **API 계약 고정**: 기존 프론트엔드(`petshop-frontend-prev`)가 기대하는 엔드포인트/요청·응답 포맷을 그대로 준수 (임의 변경 불가)
- **참고 문서**: `docs/constitution.md`(목적·성공기준·제약), `docs/specification.md`(FR/NFR/TR 요구사항 ID), `docs/plan.md`(Phase별 구현 순서), `docs/phase0-todo.md`(현재 진행 중인 Phase 0 체크리스트)
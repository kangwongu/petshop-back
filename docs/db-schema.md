# DB Schema

이 문서는 `specification.md`(FR/NFR/TR)를 기준으로 Phase 1~3(Product/Category → Order → Payment)에서 사용할 테이블 구조를 정의한다. Entity/Repository 구현 시 이 문서를 기준으로 삼는다. 실제 실행용 DDL은 `sql/schema.sql`에 있다(`infra.md`: Flyway/Liquibase 미사용, 수동 SQL 스크립트 실행 원칙).

## 공통 규칙

- PK 컬럼명은 `seq`(INT, AUTO_INCREMENT)로 통일한다.
- 생성/수정 시각은 `create_epoch`/`update_epoch`(BIGINT, epoch millis)로 관리하며 BaseTime(`@CreatedDate` 등)은 사용하지 않는다.
- Flyway/Liquibase는 사용하지 않는다. 로컬은 `ddl-auto: update`, 운영은 `ddl-auto: none` + 수동 SQL 스크립트를 원칙으로 한다.
- **FK 제약조건은 두지 않는다.** 참조 무결성은 애플리케이션(Service/도메인) 레벨에서 관리하고, 참조 컬럼에는 조회/조인 성능을 위한 인덱스만 둔다.

## 테이블

### products

상품 정보를 저장한다. FR-01(카테고리 필터링 목록 조회), FR-02(상세 조회), FR-06(초기 데이터 시딩)의 대상이다.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| seq | INT | PK, AUTO_INCREMENT | |
| category_code | INT | NOT NULL | 카테고리 코드 (1=인쇄류, 2=생활용품, 3=액세서리) |
| name | VARCHAR(100) | NOT NULL | 상품명 |
| price | BIGINT | NOT NULL | 현재가 |
| image_url | VARCHAR(500) | NULL | S3 상품 이미지 URL (TR-10) |
| create_epoch | BIGINT | NOT NULL | |
| update_epoch | BIGINT | NOT NULL | |

- 인덱스: `idx_products_category_code (category_code)` — FR-01 카테고리 필터링 조회 지원.
- **카테고리는 별도 테이블로 두지 않는다.** 카테고리 목록 조회 API가 스펙 범위 밖(프론트엔드 하드코딩, `specification.md`의 범위 제외 항목)이고 시드 데이터도 3종 고정이라, FK/조인 없이 `category_code` + 도메인 enum(`ProductCategory{ PRINTING(1,"인쇄류"), LIFESTYLE(2,"생활용품"), ACCESSORY(3,"액세서리") }`)으로 code↔한글명을 매핑해 응답의 `categoryId`/`categoryName`을 만든다. 이 코드값(1/2/3)은 프론트엔드(`petshop-frontend-prev`)가 이미 하드코딩한 값과 동일해야 한다(TR-06). 이후 카테고리 관리 기능/추가 속성이 필요해지면 그때 테이블로 승격한다.
- `image_url`은 NULL을 허용한다. 운영자가 상품 등록 후 별도로 1회성 업로드하는 흐름(TR-10)이라 등록 시점엔 비어 있을 수 있다.
- 재고 컬럼은 두지 않는다(범위 제외).

### orders

비회원 주문을 저장한다. FR-03(주문 생성) 대상이다.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| seq | INT | PK, AUTO_INCREMENT | |
| order_number | VARCHAR(64) | NOT NULL, UNIQUE | `ORD-yyyyMMddHHmmss-XXXXXX` 형식, Toss `orderId`로 그대로 사용 |
| buyer_name | VARCHAR(50) | NOT NULL | |
| buyer_phone | VARCHAR(20) | NOT NULL | |
| buyer_address | VARCHAR(255) | NOT NULL | 우편번호 + 상세주소를 합친 단일 문자열 |
| total_amount | BIGINT | NOT NULL | 생성 시점 서버 계산 총액 스냅샷 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | `PENDING` / `PAID` / `FAILED` / `CANCELLED` |
| create_epoch | BIGINT | NOT NULL | |
| update_epoch | BIGINT | NOT NULL | |

- `order_number`는 회원 없이도 주문을 식별하는 키이자 Toss 결제창에 전달하는 `orderId`다.
- `total_amount`는 FR-03(현재가 기준 총액 계산)의 계산 결과를 저장한다. 결제 승인 시 NFR-01(클라이언트 금액 대조)에서 이 컬럼과 결제 확인 요청의 `amount`를 비교한다.
- 회원 테이블은 두지 않는다(비회원 전용, 범위 제외).

### order_items

주문에 포함된 상품 항목과 주문 시점 가격 스냅샷을 저장한다.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| seq | INT | PK, AUTO_INCREMENT | |
| order_seq | INT | NOT NULL | orders.seq 참조 (FK 제약 없음) |
| product_seq | INT | NOT NULL | products.seq 참조 (FK 제약 없음) |
| quantity | INT | NOT NULL | |
| price | BIGINT | NOT NULL | 주문 시점 가격 스냅샷 (FR-03) |
| create_epoch | BIGINT | NOT NULL | |

- 인덱스: `idx_order_items_order_seq (order_seq)`, `idx_order_items_product_seq (product_seq)`.
- `price`는 `products.price`를 그대로 복사해 저장한다. 이후 상품가가 바뀌어도 과거 주문 금액은 변하지 않는다.
- 생성 후 수정되지 않는 레코드이므로 `update_epoch`는 두지 않는다.

### payments

Toss 결제 승인/웹훅 처리 결과를 저장한다. FR-04, FR-05 대상이다.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| seq | INT | PK, AUTO_INCREMENT | |
| order_seq | INT | NOT NULL, UNIQUE | orders.seq 참조 (FK 제약 없음), 1 주문 : 1 결제 |
| payment_key | VARCHAR(200) | NOT NULL, UNIQUE | Toss 발급 키, 웹훅 멱등성 기준(NFR-02) |
| amount | BIGINT | NOT NULL | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | `PENDING` / `PAID` / `FAILED` / `CANCELLED` |
| fail_reason | VARCHAR(500) | NULL | Toss 실패 코드/메시지 |
| approved_epoch | BIGINT | NULL | Toss `approvedAt`을 epoch millis로 변환한 값 |
| create_epoch | BIGINT | NOT NULL | |
| update_epoch | BIGINT | NOT NULL | |

- `payment_key`의 UNIQUE 제약이 웹훅 멱등 처리의 실질적 가드다(FR-05, NFR-02). 애플리케이션 계층에서도 저장 전 존재 여부를 확인한다.
- `order_seq`를 UNIQUE로 두어 1:1 관계를 강제한다(재시도로 인한 중복 결제 레코드 방지).

## 관계 요약

```
products (category_code는 도메인 enum 코드, 테이블 아님)
orders   (1) ── (N) order_items ── (N:1) products
orders   (1) ── (1) payments
```

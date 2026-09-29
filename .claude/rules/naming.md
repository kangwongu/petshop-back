# 네이밍 컨벤션

## 개요

Java로 작성된 프로젝트에서 사용하기 위한 명명 규칙입니다.

명명 컨벤션을 지키면 유지보수성과 가독성을 향상시킵니다.

---

## 코드 스타일 기본 규칙

- **1탭 4스페이스**를 권장합니다.
    - 공백일 경우 스페이스가 아닌 1탭을 권장합니다.
- **하나의 행은 최대 100자를 권장**합니다. 100자 이상이 될 때 줄바꿈을 해주세요.
    - 유사 타입별로 묶어서 개행
    - 예외 케이스
        - javadoc, url 등 열 제한을 따를 수 없는 행
        - import 문
        - command line

---

## 파일명

| 계층 | 패턴 |
|---|---|
| UseCase (in port) | `Get\|Create\|Modify\|Delete~UseCase` |
| Port (out port) | `Get\|Save\|Delete~Port` |
| Service | `~ReadService` / `~WriteService` |
| Mapper | `~Mapper` |
| JPA Entity | `~JpaEntity` |

## Request / Response DTO

- HTTP 요청: `Get|Create|Modify|Delete~Req` — Upsert는 `Create`로 통일
- HTTP 응답: `~Res`, 미리보기는 `~PreviewRes`
- 예외(동사형): `LoginReq`, `CacheReq` 등
- Service ↔ Controller 경계: `~ReqCommand` / `~ResCommand`

## 메서드명

- **camelCase**을 사용하며, **동사로 시작**합니다.
- 메소드명은 JPA query method에는 적용하지 않습니다.
    - **단,** JPQL 혹은 QueryDSL로 작성한 메소드에만 적용합니다.

### CUD (Create, Update, Delete)

- 생성: `create` / 수정: `modify` / 삭제(Soft): `delete` / DB 완전 삭제: `hardDelete`
- Port 인터페이스: `Get`, `Save`(Update 포함), `Delete`만 사용
- **단,** 도메인 상태 전이 메소드(상태머신 전이를 표현하는 유비쿼터스 언어)는 예외
  - 예) `confirm()`(결제 승인), `cancel()`, `expire()`

### 조회 (`find*`)

- 기본적으로 `find*`로 통일합니다.
- **예외 발생**: `findBySeq` (NotFoundException 던질 수 있음)
- **null 반환 가능**: `findNullableBySeq`
- **Page 반환**: `findPage`
- **List 반환**: `findList`
- **Map 반환**: 복수형 사용 (예: `findProducts`, `findOrders`)

**반환하는 객체가 도메인이름과 다를 때:**
- `find{객체이름}By{parameter}` 형식 사용
- 예) `findProductBySeq`, `findOrderList`
- **단,** 반환되는 객체가 복수(List, Map, Page)이면서 도메인 이름과 같지 않은 경우:
  - `find{객체이름}{List, Map, Page}By{parameter}` 형식
  - 예) `findProductListByCategorySeqList`

**Port/UseCase에서의 메서드명:**
- 반환되는 객체가 UseCase/Port에 있는 경우 메소드명에서 도메인명 중복 금지
  - ✅ `getProductUseCase.findBySeq(seq)`
  - ❌ `getProductUseCase.findProductBySeq(seq)`
- 예시:
  - Product 도메인 UseCase: `getProductUseCase.findBySeq(seq)` (NOT `findProductBySeq`)
  - Product 도메인 Port: `getProductPort.findBySeq(seq)`
  - Product 도메인 UseCase (복수): `getProductUseCase.findListByCategorySeq(categorySeq)` (NOT `findProductListByCategorySeq`)

### Mapper

- `mapTo{변환대상}` 형식으로 작성합니다.
  - 예) `mapToJpaEntity`, `mapToDomain`
- **단,** 계층 간 객체 변환 시 `from`을 접두사로 사용할 수도 있습니다.
  - 예) `fromDomain`, `fromCommand`

## 변수명

- List: `productList` / Map, Set 등 복수형: `products`
- 상수: `UPPER_CASE_WITH_UNDERSCORE`

## 람다

- 람다 약어 혹은 DSL 빌더 람다 약어를 사용하지 않습니다.

## Import 규칙

- **와일드카드(`*`) 사용** — 마지막 패키지에서 5개 이상 import 시 와일드카드(`*`) 처리
- **Static import와 Non-static import 블록 구분**

```java
import com.ddungyomi.petshop.common.util.StringUtil;

import static com.ddungyomi.petshop.domain.product.application.service.ProductWriteService.DEFAULT_PAGE_SIZE;
```

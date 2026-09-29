# Java Patterns

## 헥사고날 아키텍처 규칙

### 계층별 의존방향

- 의존 방향: 외부 → 내부 (Adapter → Application → Domain)
- 의존방향이 역으로 되는 상황을 만들면 안 됩니다.

```
┌──────────────────────────────────────────┐
│ Adapter (Web, Persistence, External)     │
├──────────────────────────────────────────┤
│ Application (UseCase, Port, Service)     │
├──────────────────────────────────────────┤
│ Domain (Entity, Value Object)            │
└──────────────────────────────────────────┘
       ↓ (의존 방향: 한 방향만)
```

### 특정 도메인에서 타 도메인 의존 방식

**Service 계층**: UseCase 또는 Port 모두 사용 가능
```java
@Service
@RequiredArgsConstructor
public class OrderWriteService implements CreateOrderUseCase {
    private final SaveOrderPort saveOrderPort;

    // 타 도메인 의존 (Port, UseCase 둘 다 가능) — 주문 시점 상품 가격 조회
    private final GetProductPort getProductPort;
    private final GetProductUseCase getProductUseCase;
}
```

**Adapter (Persistence) 계층**: Port만 사용
```java
@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements SaveOrderPort {
    private final OrderJpaRepository orderJpaRepository;

    // 타 도메인 의존 (Port만 사용 가능)
    private final GetProductPort getProductPort;

    // ❌ 이건 금지: 타 도메인의 Adapter 직접 참조
    // private final ProductPersistenceAdapter productAdapter;
}
```

---

## 객체 모델링 규칙

### DTO 생성 규칙

- DTO는 **record 클래스**를 사용하는 것을 권장합니다.
- record 클래스는 보일러플레이트를 줄여주며, 생성자를 통해 체이닝이 가능합니다.

### Record 클래스 기본 규칙

- record 클래스는 **builder 패턴을 사용하지 않습니다**.
- 생성자를 통해 데이터를 직접 주입합니다.
- Req DTO를 사용할 때 **Spring Validation** 사용을 권장합니다.

```java
public record CreateOrderReq(
        @NotBlank
        String buyerName,
        @NotBlank
        String buyerPhone,
        @NotBlank
        String buyerAddress,
        @NotEmpty
        List<OrderItemReq> itemList
) {
}
```

### Application 계층 DTO (Command)

- 서비스 내부에서 사용하는 DTO는 **command**로 명칭합니다.
- record 클래스를 활용하여 원하는 데이터 객체로 변환합니다.

```java
public record CreateOrderReqCommand(
        String buyerName,
        String buyerPhone,
        String buyerAddress,
        List<OrderItemReqCommand> itemList
) {
}

public record CreateOrderReq(
        String buyerName,
        String buyerPhone,
        String buyerAddress,
        List<OrderItemReq> itemList
) {
    public CreateOrderReqCommand toCommand() {
        return new CreateOrderReqCommand(
                this.buyerName, this.buyerPhone, this.buyerAddress,
                this.itemList.stream().map(OrderItemReq::toCommand).toList()
        );
    }
}
```

### Domain 객체 규칙

- **Record 사용**으로 불변성 보장합니다.
- 생성 시 null 검증 코드는 **if를 지양**하고 한 줄 코드로 작성합니다.
- 비즈니스 로직은 응집도를 위해 **최대한 Domain 안에서** 진행합니다.
- WithSeq 메소드를 사용하지 않고 **new 생성을 기본**으로 사용합니다.
  - `create` — 새 객체 생성
  - `modify` → `modifyPrice`, `modifyName` — 특정 필드만 수정
  - `delete` — 삭제 (상태 변경)

```java
public record Product(
        Integer seq,
        Integer categorySeq,
        String name,
        Long price,
        Long createEpoch,
        Long updateEpoch
) {
    public Product {
        price = Objects.requireNonNullElse(price, 0L);
    }

    public static Product create(Integer categorySeq, String name, Long price) {
        return new Product(null, categorySeq, name, price, System.currentTimeMillis(), System.currentTimeMillis());
    }

    public Product modifyPrice(Long price) {
        return new Product(this.seq, this.categorySeq, this.name, price, this.createEpoch, System.currentTimeMillis());
    }
}
```

### JPA Entity 규칙

- **class를 사용**합니다 (record 아님).
- Entity 생성시, **`@Column` 어노테이션**을 사용하여 DB 스키마를 코드로 파악할 수 있게 합니다.
- **BaseTime을 지양**하고 epoch 관련은 Entity에 직접 필드로 사용합니다.
- 생성 메소드명은 **Builder pattern을 사용하지 않고 `create` 메소드**로 통일합니다.
- **Column 어노테이션 사용 시**, default값이 아닐 경우에만 작성합니다.

```java
@Entity
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(nullable = false)
    private Integer categorySeq;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    private Long createEpoch;
    private Long updateEpoch;

    public static ProductJpaEntity create(Integer categorySeq, String name, Long price) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.categorySeq = categorySeq;
        entity.name = name;
        entity.price = price;
        entity.createEpoch = System.currentTimeMillis();
        entity.updateEpoch = System.currentTimeMillis();
        return entity;
    }
}
```

---

## Repository Pattern

### Jpa (RDB)
- JpaEntity로 작성이 Entity는 JpaRepository를 사용합니다.
```java
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Integer> {
    List<ProductJpaEntity> findListByCategorySeq(Integer categorySeq);

    Page<ProductJpaEntity> findPageByCategorySeq(Integer categorySeq, Pageable pageable);
}
```

### QueryDSL (RDB - 복잡한 쿼리)
- 단순 조회는 JpaRepository 메서드 컨벤션을 우선 사용하고, 복잡한 쿼리에 QueryDSL을 사용합니다.
- 현재 프로젝트에는 QueryDSL 의존성이 아직 없으며, 단순 카테고리 필터링 수준은 JpaRepository 메서드 쿼리로 충분합니다.
- 복잡한 쿼리(동적 조건, 집계 등)가 실제로 필요해지는 시점에 의존성을 추가하고 아래 패턴을 적용합니다.
- QueryDSL Repository 이름 앞에는 `Custom`이 붙습니다.
- 인터페이스, 구현체, JpaRepository를 분리합니다.
```java
// 1. Custom 인터페이스
public interface ProductJpaCustomRepository {
    Map<Integer, Long> countByCategorySeqIn(List<Integer> categorySeqs);
    Boolean existsByCategorySeqAndName(Integer categorySeq, String name);
}

// 2. 구현체
@Repository
@RequiredArgsConstructor
public class ProductJpaCustomRepositoryImpl implements ProductJpaCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, Long> countByCategorySeqIn(List<Integer> categorySeqs) {
        return queryFactory
                .from(qProduct)
                .where(qProduct.categorySeq.in(categorySeqs))
                .groupBy(qProduct.categorySeq)
                .transform(GroupBy.groupBy(qProduct.categorySeq)
                        .as(qProduct.categorySeq.count()));
    }

    @Override
    public Boolean existsByCategorySeqAndName(Integer categorySeq, String name) {
        return queryFactory
                .selectFrom(qProduct)
                .where(qProduct.categorySeq.eq(categorySeq)
                        .and(qProduct.name.eq(name)))
                .fetchFirst() != null;
    }
}

// 3. 통합 Repository
public interface ProductJpaRepository
        extends JpaRepository<ProductJpaEntity, Integer>, ProductJpaCustomRepository {
}
```

## Service Layer
- Service는 데이터를 조회(Read)하는 서비스와 Data에 영향을 주는 (Create, Update, Delete)하는 서비스로만 구분합니다.
- 데이터 조회 서비스는 `ReadService` 이며, 영향을 주는 서비스는 `WriteService`입니다.
- adapter Layer와 통신을 하는 중간 객체는 `use case` / `port` 를 통해서만 통신합니다.
- `use case`의 구현체를 담당합니다.
### `ReadService`
```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductReadService implements GetProductUseCase {
    private final GetProductPort getProductPort;

    @Override
    public ProductResCommand findBySeq(Integer seq) {
        return ProductResCommand.from(
                getProductPort.findBySeq(seq)
        );
    }

    @Override
    public Page<ProductResCommand> findPage(GetProductReqCommand command) {
        return ProductResCommand.from(
                getProductPort.findPage(
                        command.categorySeq(), command.pageable()
                )
        );
    }
}
```

### `WriteService`
```java
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderWriteService implements CreateOrderUseCase {
    private final SaveOrderPort saveOrderPort;
    private final GetProductPort getProductPort;

    @Override
    public OrderResCommand create(CreateOrderReqCommand command) {
        // 상품 현재가 조회 후 주문 총액 계산 및 가격 스냅샷 저장은 Domain에서 처리
        return OrderResCommand.from(saveOrderPort.save(command));
    }
}
```

## Constructor Injection
- 항상 `@RequiredArgsConstructor`를 사용합니다.
```java
// GOOD - constructor injection
@RequiredArgsConstructor
public class ProductReadService {
    private final GetProductPort getProductPort;
}

// BAD - field injection
public class ProductReadService {
    @Autowired // or @Inject
    private GetProductPort getProductPort;
}
```

---

## DTO Mapping
### Mapper
- Mapper는 Persistence Layer와 Service Layer의 통신에서만 사용합니다.
```java
public class ProductMapper {

    public static Product mapToDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getSeq(), entity.getCategorySeq(),
                entity.getName(), entity.getPrice(),
                entity.getCreateEpoch(), entity.getUpdateEpoch()
        );
    }

    public static ProductJpaEntity mapToJpaEntity(Product product) {
        return ProductJpaEntity.create(
                product.categorySeq(), product.name(), product.price()
        );
    }
}
```

### Command
#### `ReqCommand`
- `ReqCommand` 는 Controller에서 받은 Client의 요청 데이터(Query param, Body, Header)를 Service Layer에 전달해주는 역할에만 사용합니다.
- `from` 메소드를 통해 객체를 만들어 불변성을 `record`로 보장해줍니다.
```java
public record CreateOrderReqCommand(
        String buyerName,
        String buyerPhone,
        String buyerAddress,
        List<OrderItemReqCommand> itemList
) {
    // GOOD - from method and request data form parameter
    public static CreateOrderReqCommand from(
            String buyerName, String buyerPhone, String buyerAddress,
            List<OrderItemReqCommand> itemList
    ) {
        return new CreateOrderReqCommand(buyerName, buyerPhone, buyerAddress, itemList);
    }

    // BAD - not use from method and request data from parameter
    public static CreateOrderReqCommand of(CreateOrderReq req) {
        return new CreateOrderReqCommand(
                req.buyerName(), req.buyerPhone(), req.buyerAddress(),
                req.itemList().stream().map(OrderItemReq::toCommand).toList()
        );
    }
}
```

#### `ResCommand`
- `ResCommand`는 Service Layer에서 Controller로 데이터를 반환할 때만 사용합니다.
- `from` 메소드를 통해 객체를 만들어 불변성을 `record`로 보장해줍니다.
```java
public record ProductResCommand(
        Integer seq,
        Integer categorySeq,
        String name,
        Long price,
        Long createEpoch
) {
    public static ProductResCommand from(Product product) {
        return new ProductResCommand(
                product.seq(), product.categorySeq(),
                product.name(), product.price(),
                product.createEpoch()
        );
    }
}
```

### Controller Layer (API Request / Response)
```java
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final GetProductUseCase getProductUseCase;

    @GetMapping("/{seq}")
    public BaseRes<ProductRes> findBySeq(
            @PathVariable Integer seq
    ) {
        return BaseRes.from(ProductRes.fromCommand(
                getProductUseCase.findBySeq(seq)
        ));
    }

    @GetMapping
    public BaseRes<PaginateData<ProductRes>> findPage(
            @Valid @ParameterObject GetProductReq req
    ) {
        return BaseRes.from(
                getProductUseCase.findPage(req.toCommand())
                        .mapToContent(ProductRes::fromCommand)
        );
    }
}
```
#### `Req` DTO
- Client에서 들어온 요청 (Query param, Body)에서만 사용합니다.
- `toCommand` method를 구현하여 `ReqCommand` 를 사용합니다.
- `toCommand` 는 parameter를 받을 수 있으나, Header 혹은 access token 안에 있는 값과 같은 경우에만 parameter로 받을 수 있습니다.
```java
@Schema(description = "상품 목록 조회 Query String DTO")
public record GetProductReq(
        @Schema(description = "카테고리 SEQ", example = "1")
        Integer categorySeq
) {
    public GetProductReqCommand toCommand() {
        return GetProductReqCommand.create(this.categorySeq);
    }
}
```

#### `Res` DTO
```java
@Schema(description = "상품 정보 반환 DTO")
public record ProductRes(
        @Schema(description = "상품 SEQ", example = "1")
        Integer seq,

        @Schema(description = "카테고리 SEQ", example = "1")
        Integer categorySeq,

        @Schema(description = "상품명", example = "뚱이요미 인형")
        String name,

        @Schema(description = "가격", example = "15000")
        Long price
) {
    public static ProductRes fromCommand(ProductResCommand command) {
        return new ProductRes(
                command.seq(), command.categorySeq(),
                command.name(), command.price()
        );
    }
}
```

### PaginateData
- Page 객체를 반환할 때는 PaginateData로 반환을 해야됩니다.
- PaginateData로의 반환은 Service Layer에서 Controller Layer로 반환이 됩니다.
- Controller에서 PaginateData 내부 함수인 `mapToContent` 메소드를 활용해 `ResCommand`를 `Res` DTO로 변환합니다.
```java
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final GetProductUseCase getProductUseCase;

    @GetMapping
    public BaseRes<PaginateData<ProductRes>> findPage(
            @Valid @ParameterObject GetProductReq req
    ) {
        return BaseRes.from(
                getProductUseCase.findPage(req.toCommand())
                        .mapToContent(ProductRes::fromCommand)
        );
    }
}
```

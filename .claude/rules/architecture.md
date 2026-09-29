# 패키지 구조

```text
domain/{product|order|payment}/
├── adapter/
│   ├── in/
│   │   └── web/                # HTTP Controller
│   │       ├── ~Controller.java
│   │       └── dto/{req|res}/
│   └── out/
│       ├── client/             # 도메인 전용 외부 API (payment: Toss Payments RestClient)
│       └── persistence/
│           ├── ~PersistenceAdapter.java
│           ├── entity/~JpaEntity.java
│           ├── mapper/~Mapper.java
│           └── repository/~JpaRepository.java
├── application/
│   ├── command/
│   │   ├── req/~ReqCommand.java
│   │   └── res/~ResCommand.java
│   ├── port/
│   │   ├── in/  Get|Create|Modify|Delete~UseCase.java
│   │   └── out/ Get|Save|Delete~Port.java
│   └── service/
│       ├── ~ReadService.java
│       └── ~WriteService.java
├── domain/~.java
└── shared/                     # 도메인 내 공용 Enum/상수
```

## 의존 방향

`adapter → application → domain` (단방향, 역전 금지)

- 도메인 전용 외부 API는 `adapter/out/client/` (예: payment 도메인의 Toss Payments 연동)
- 컨트롤러는 UseCase만 호출, 비즈니스 로직 구현 금지
- 서비스는 `port.in/out`에만 의존, 영속성/외부 통신은 어댑터가 구현

## 타 도메인 의존

- **Service**: 타 도메인 UseCase 또는 Port 모두 사용 가능
- **Adapter(Persistence)**: 타 도메인 Port만 사용

```java
// Service에서 타 도메인 의존 (예: order가 product를 참조)
private final GetProductPort getProductPort;       // Port 의존 ✅
private final GetProductUseCase getProductUseCase; // UseCase 의존 ✅

// PersistenceAdapter에서 타 도메인 의존
private final GetProductPort getProductPort;       // Port만 ✅
```

-- petshop-back DDL
-- 설계 근거/컬럼 설명은 docs/db-schema.md 참고
-- 대상: MySQL 8.4 (로컬 Docker Compose / 운영 AWS RDS 공통)
-- Flyway/Liquibase 미사용 — 이 스크립트는 수동으로 실행한다 (.claude/rules/infra.md)
-- FK 제약조건은 두지 않는다. 참조 무결성은 애플리케이션 레벨에서 관리하고,
-- 각 참조 컬럼에는 조회/조인 성능을 위한 인덱스만 둔다.

CREATE TABLE products (
    seq             INT AUTO_INCREMENT PRIMARY KEY,
    category_code   INT          NOT NULL,
    name            VARCHAR(100) NOT NULL,
    price           BIGINT       NOT NULL,
    image_url       VARCHAR(500) NULL,
    create_epoch    BIGINT       NOT NULL,
    update_epoch    BIGINT       NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_products_category_code ON products (category_code);

CREATE TABLE orders (
    seq             INT AUTO_INCREMENT PRIMARY KEY,
    order_number    VARCHAR(64)  NOT NULL,
    buyer_name      VARCHAR(50)  NOT NULL,
    buyer_phone     VARCHAR(20)  NOT NULL,
    buyer_address   VARCHAR(255) NOT NULL,
    total_amount    BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    create_epoch    BIGINT       NOT NULL,
    update_epoch    BIGINT       NOT NULL,
    CONSTRAINT uk_orders_order_number UNIQUE (order_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_items (
    seq             INT AUTO_INCREMENT PRIMARY KEY,
    order_seq       INT    NOT NULL,
    product_seq     INT    NOT NULL,
    quantity        INT    NOT NULL,
    price           BIGINT NOT NULL,
    create_epoch    BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_order_items_order_seq ON order_items (order_seq);
CREATE INDEX idx_order_items_product_seq ON order_items (product_seq);

CREATE TABLE payments (
    seq             INT AUTO_INCREMENT PRIMARY KEY,
    order_seq       INT          NOT NULL,
    payment_key     VARCHAR(200) NOT NULL,
    amount          BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    fail_reason     VARCHAR(500) NULL,
    approved_epoch  BIGINT       NULL,
    create_epoch    BIGINT       NOT NULL,
    update_epoch    BIGINT       NOT NULL,
    CONSTRAINT uk_payments_order_seq   UNIQUE (order_seq),
    CONSTRAINT uk_payments_payment_key UNIQUE (payment_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

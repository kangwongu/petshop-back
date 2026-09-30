package com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order_items_order_seq", columnList = "order_seq"),
        @Index(name = "idx_order_items_product_seq", columnList = "product_seq")
})
public class OrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "order_seq", nullable = false)
    private Integer orderSeq;

    @Column(name = "product_seq", nullable = false)
    private Integer productSeq;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long createEpoch;

    public static OrderItemJpaEntity create(
            Integer orderSeq, Integer productSeq, Integer quantity, Long price, Long createEpoch
    ) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.orderSeq = orderSeq;
        entity.productSeq = productSeq;
        entity.quantity = quantity;
        entity.price = price;
        entity.createEpoch = createEpoch;
        return entity;
    }
}

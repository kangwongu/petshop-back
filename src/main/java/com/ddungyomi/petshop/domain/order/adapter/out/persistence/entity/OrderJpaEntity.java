package com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.ddungyomi.petshop.domain.order.domain.OrderStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders",
        uniqueConstraints = @UniqueConstraint(name = "uk_orders_order_number", columnNames = "order_number"))
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "order_number", nullable = false, length = 64)
    private String orderNumber;

    @Column(name = "buyer_name", nullable = false, length = 50)
    private String buyerName;

    @Column(name = "buyer_phone", nullable = false, length = 20)
    private String buyerPhone;

    @Column(name = "buyer_address", nullable = false, length = 255)
    private String buyerAddress;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private Long createEpoch;

    @Column(nullable = false)
    private Long updateEpoch;

    public static OrderJpaEntity create(
            Integer seq, String orderNumber, String buyerName, String buyerPhone, String buyerAddress,
            Long totalAmount, OrderStatus status, Long createEpoch, Long updateEpoch
    ) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.seq = seq;
        entity.orderNumber = orderNumber;
        entity.buyerName = buyerName;
        entity.buyerPhone = buyerPhone;
        entity.buyerAddress = buyerAddress;
        entity.totalAmount = totalAmount;
        entity.status = status;
        entity.createEpoch = createEpoch;
        entity.updateEpoch = updateEpoch;
        return entity;
    }
}

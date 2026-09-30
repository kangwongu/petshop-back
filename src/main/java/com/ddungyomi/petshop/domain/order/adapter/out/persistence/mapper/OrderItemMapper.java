package com.ddungyomi.petshop.domain.order.adapter.out.persistence.mapper;

import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.ddungyomi.petshop.domain.order.domain.OrderItem;

public class OrderItemMapper {

    public static OrderItem mapToDomain(OrderItemJpaEntity entity) {
        return new OrderItem(
                entity.getSeq(),
                entity.getOrderSeq(),
                entity.getProductSeq(),
                entity.getQuantity(),
                entity.getPrice(),
                entity.getCreateEpoch()
        );
    }

    public static OrderItemJpaEntity mapToJpaEntity(OrderItem item, Integer orderSeq) {
        return OrderItemJpaEntity.create(
                orderSeq,
                item.productSeq(),
                item.quantity(),
                item.price(),
                item.createEpoch()
        );
    }
}

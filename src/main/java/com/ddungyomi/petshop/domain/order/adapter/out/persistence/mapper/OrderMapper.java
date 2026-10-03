package com.ddungyomi.petshop.domain.order.adapter.out.persistence.mapper;

import java.util.List;

import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.order.domain.OrderItem;

public class OrderMapper {

    public static Order mapToDomain(OrderJpaEntity entity, List<OrderItem> orderItemList) {
        return new Order(
                entity.getSeq(),
                entity.getOrderNumber(),
                entity.getBuyerName(),
                entity.getBuyerPhone(),
                entity.getBuyerAddress(),
                entity.getTotalAmount(),
                entity.getStatus(),
                orderItemList,
                entity.getCreateEpoch(),
                entity.getUpdateEpoch()
        );
    }

    public static OrderJpaEntity mapToJpaEntity(Order order) {
        return OrderJpaEntity.create(
                order.seq(),
                order.orderNumber(),
                order.buyerName(),
                order.buyerPhone(),
                order.buyerAddress(),
                order.totalAmount(),
                order.status(),
                order.createEpoch(),
                order.updateEpoch()
        );
    }
}

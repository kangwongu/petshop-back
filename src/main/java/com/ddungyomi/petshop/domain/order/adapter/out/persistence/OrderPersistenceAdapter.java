package com.ddungyomi.petshop.domain.order.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.mapper.OrderItemMapper;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.mapper.OrderMapper;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.repository.OrderItemJpaRepository;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.repository.OrderJpaRepository;
import com.ddungyomi.petshop.domain.order.application.port.out.SaveOrderPort;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.order.domain.OrderItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements SaveOrderPort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity savedOrderEntity = orderJpaRepository.save(OrderMapper.mapToJpaEntity(order));

        List<OrderItemJpaEntity> orderItemEntityList = order.orderItemList().stream()
                .map(item -> OrderItemMapper.mapToJpaEntity(item, savedOrderEntity.getSeq()))
                .toList();
        List<OrderItem> savedOrderItemList = orderItemJpaRepository.saveAll(orderItemEntityList).stream()
                .map(OrderItemMapper::mapToDomain)
                .toList();

        return OrderMapper.mapToDomain(savedOrderEntity, savedOrderItemList);
    }
}

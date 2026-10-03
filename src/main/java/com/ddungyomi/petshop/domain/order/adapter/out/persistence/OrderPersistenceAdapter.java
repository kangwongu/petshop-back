package com.ddungyomi.petshop.domain.order.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ddungyomi.petshop.common.exception.NotFoundException;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderJpaEntity;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.mapper.OrderItemMapper;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.mapper.OrderMapper;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.repository.OrderItemJpaRepository;
import com.ddungyomi.petshop.domain.order.adapter.out.persistence.repository.OrderJpaRepository;
import com.ddungyomi.petshop.domain.order.application.port.out.GetOrderPort;
import com.ddungyomi.petshop.domain.order.application.port.out.SaveOrderPort;
import com.ddungyomi.petshop.domain.order.domain.Order;
import com.ddungyomi.petshop.domain.order.domain.OrderItem;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements SaveOrderPort, GetOrderPort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Order findByOrderNumber(String orderNumber) {
        OrderJpaEntity orderEntity = orderJpaRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다. orderNumber=" + orderNumber));
        List<OrderItem> orderItemList = orderItemJpaRepository.findListByOrderSeq(orderEntity.getSeq()).stream()
                .map(OrderItemMapper::mapToDomain)
                .toList();

        return OrderMapper.mapToDomain(orderEntity, orderItemList);
    }

    @Override
    public Order save(Order order) {
        // seq가 있으면 JPA가 merge(update)로, 없으면 persist(insert)로 처리한다
        OrderJpaEntity savedOrderEntity = orderJpaRepository.save(OrderMapper.mapToJpaEntity(order));

        if (order.seq() != null) {
            return OrderMapper.mapToDomain(savedOrderEntity, order.orderItemList());
        }

        List<OrderItemJpaEntity> orderItemEntityList = order.orderItemList().stream()
                .map(item -> OrderItemMapper.mapToJpaEntity(item, savedOrderEntity.getSeq()))
                .toList();
        List<OrderItem> savedOrderItemList = orderItemJpaRepository.saveAll(orderItemEntityList).stream()
                .map(OrderItemMapper::mapToDomain)
                .toList();

        return OrderMapper.mapToDomain(savedOrderEntity, savedOrderItemList);
    }
}

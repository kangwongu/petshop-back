package com.ddungyomi.petshop.domain.order.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderItemJpaEntity;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Integer> {
}

package com.ddungyomi.petshop.domain.order.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddungyomi.petshop.domain.order.adapter.out.persistence.entity.OrderJpaEntity;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Integer> {
}

package com.ddungyomi.petshop.domain.payment.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddungyomi.petshop.domain.payment.adapter.out.persistence.entity.PaymentJpaEntity;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Integer> {

    Optional<PaymentJpaEntity> findByPaymentKey(String paymentKey);
}

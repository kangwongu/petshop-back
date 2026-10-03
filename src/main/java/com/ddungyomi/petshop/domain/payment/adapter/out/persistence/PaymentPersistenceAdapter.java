package com.ddungyomi.petshop.domain.payment.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.ddungyomi.petshop.domain.payment.adapter.out.persistence.mapper.PaymentMapper;
import com.ddungyomi.petshop.domain.payment.adapter.out.persistence.repository.PaymentJpaRepository;
import com.ddungyomi.petshop.domain.payment.application.port.out.GetPaymentPort;
import com.ddungyomi.petshop.domain.payment.application.port.out.SavePaymentPort;
import com.ddungyomi.petshop.domain.payment.domain.Payment;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements SavePaymentPort, GetPaymentPort {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment findNullableByPaymentKey(String paymentKey) {
        return paymentJpaRepository.findByPaymentKey(paymentKey)
                .map(PaymentMapper::mapToDomain)
                .orElse(null);
    }

    @Override
    public Payment save(Payment payment) {
        // seq가 있으면 JPA가 merge(update)로, 없으면 persist(insert)로 처리한다
        return PaymentMapper.mapToDomain(paymentJpaRepository.save(PaymentMapper.mapToJpaEntity(payment)));
    }
}

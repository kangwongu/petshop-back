package com.ddungyomi.petshop.domain.payment.adapter.out.persistence.mapper;

import com.ddungyomi.petshop.domain.payment.adapter.out.persistence.entity.PaymentJpaEntity;
import com.ddungyomi.petshop.domain.payment.domain.Payment;

public class PaymentMapper {

    public static Payment mapToDomain(PaymentJpaEntity entity) {
        return new Payment(
                entity.getSeq(),
                entity.getOrderSeq(),
                entity.getPaymentKey(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getFailReason(),
                entity.getApprovedEpoch(),
                entity.getCreateEpoch(),
                entity.getUpdateEpoch()
        );
    }

    public static PaymentJpaEntity mapToJpaEntity(Payment payment) {
        return PaymentJpaEntity.create(
                payment.seq(),
                payment.orderSeq(),
                payment.paymentKey(),
                payment.amount(),
                payment.status(),
                payment.failReason(),
                payment.approvedEpoch(),
                payment.createEpoch(),
                payment.updateEpoch()
        );
    }
}
